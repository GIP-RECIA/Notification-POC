package fr.recia.notifications.delayer.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.notifications.delayer.configuration.KafkaNotificationProperties;
import fr.recia.notifications.delayer.droitDeconnexionConfig.Region;
import fr.recia.notifications.delayer.services.DroitDeconnexionService;
import fr.recia.notifications.delayer.services.LdapBypassDroitDeconnexionService;
import fr.recia.notifications.delayer.services.LdapRegionService;
import fr.recia.notifications.model_kafka.model.Content;
import fr.recia.notifications.model_kafka.model.EventHeader;
import fr.recia.notifications.model_kafka.model.Notification;
import fr.recia.notifications.model_kafka.model.NotificationHeader;
import fr.recia.notifications.model_kafka.model.RoutedNotification;
import fr.recia.notifications.model_kafka_serde.model.*;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ProcessorDelayerTest {

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, RoutedNotification> inputTopic;
    private TestOutputTopic<String, RoutedNotification> outputWebTopic;
    private TestOutputTopic<String, RoutedNotification> outputDltTopic;

    private DroitDeconnexionService droitDeconnexionService;
    private LdapRegionService ldapRegionService;
    private LdapBypassDroitDeconnexionService ldapBypassDroitDeconnexionService;
    private KeyValueStore<String, RoutedNotification> stateStore;

    private final static String STORE_NAME = "delayer-store";
    private final static String ROUTED_TOPIC_WEB = "notifications.web";

    @BeforeEach
    void setUp() {
        droitDeconnexionService = Mockito.mock(DroitDeconnexionService.class);
        ldapRegionService = Mockito.mock(LdapRegionService.class);
        ldapBypassDroitDeconnexionService = Mockito.mock(LdapBypassDroitDeconnexionService.class);
        KafkaNotificationProperties kafkaNotificationProperties = new KafkaNotificationProperties();

        kafkaNotificationProperties.setRouter("notifications.router");
        kafkaNotificationProperties.setReplayer("notifications.replayer");
        kafkaNotificationProperties.setWeb("notifications.web");
        kafkaNotificationProperties.setMail("notifications.mail");
        kafkaNotificationProperties.setPush("notifications.push");
        kafkaNotificationProperties.setDlt("notifications.dlt");

        kafkaNotificationProperties.setSinkWeb("sink.web");
        kafkaNotificationProperties.setSinkMail("sink.mail");
        kafkaNotificationProperties.setSinkPush("sink.push");
        kafkaNotificationProperties.setSinkDlt("sink.dlt");

        kafkaNotificationProperties.setStore("delayer-store");
        kafkaNotificationProperties.setProcessor("processor-delayer");
        kafkaNotificationProperties.setSourceRouter("router");
        kafkaNotificationProperties.setSourceReplayer("replayer");
        kafkaNotificationProperties.setRetries(5);

        // Comportement par défaut pour le bypass
        when(ldapBypassDroitDeconnexionService.canBypass(anyString())).thenReturn(false);

        Topology topology = new Topology();
        ObjectMapper objectMapper = new ObjectMapper();

        Serde<String> stringSerde = Serdes.String();
        Serde<RoutedNotification> routedNotificationSerde = new RoutedNotificationSerde(objectMapper);

        Serializer<String> stringSerializer = stringSerde.serializer();
        Deserializer<String> stringDeserializer = stringSerde.deserializer();
        Serializer<RoutedNotification> routedSerializer = routedNotificationSerde.serializer();
        Deserializer<RoutedNotification> routedDeserializer = routedNotificationSerde.deserializer();

        topology.addSource("Source", stringDeserializer, routedDeserializer, "input-topic");

        ProcessorSupplier<String, RoutedNotification, String, RoutedNotification> processorSupplier =
                () -> {
                    ProcessorDelayer processor = new ProcessorDelayer(droitDeconnexionService, ldapRegionService, ldapBypassDroitDeconnexionService);
                    // FIX : Ajout de la fréquence pour éviter le NullPointerException dans le Punctuator
                    processor.setScanFrequency(Duration.ofSeconds(60));
                    processor.setKafkaNotificationProperties(kafkaNotificationProperties);
                    return processor;
                };

        topology.addProcessor("ProcessDelayer", processorSupplier, "Source");

        topology.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore(STORE_NAME),
                        stringSerde,
                        routedNotificationSerde
                ),
                "ProcessDelayer"
        );

        topology.addSink("sink.web", "output-topic-web", stringSerializer, routedSerializer, "ProcessDelayer");
        topology.addSink("sink.mail", "output-topic-mail", stringSerializer, routedSerializer, "ProcessDelayer");
        topology.addSink("sink.push", "output-topic-push", stringSerializer, routedSerializer, "ProcessDelayer");
        topology.addSink("sink.dlt", "output-topic-dlt", stringSerializer, routedSerializer, "ProcessDelayer");

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-delayer");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        testDriver = new TopologyTestDriver(topology, props);

        inputTopic = testDriver.createInputTopic("input-topic", stringSerializer, routedSerializer);
        outputWebTopic = testDriver.createOutputTopic("output-topic-web", stringDeserializer, routedDeserializer);
        outputDltTopic = testDriver.createOutputTopic("output-topic-dlt", stringDeserializer, routedDeserializer);

        stateStore = testDriver.getKeyValueStore(STORE_NAME);
    }

    @AfterEach
    void tearDown() {
        if (testDriver != null) {
            testDriver.close();
        }
    }

    @Test
    @DisplayName("Processor : Quand l'utilisateur a le droit, la notification va directement vers son Sink")
    void testProcessNotificationImmediate() {
        String userId = "user123";
        long now = System.currentTimeMillis();

        when(ldapRegionService.getRegionByUid(userId)).thenReturn(Region.CENTRE);
        when(ldapRegionService.getListDomaineCentre(userId)).thenReturn(Collections.singletonList("lycees.netocentre.fr"));
        when(droitDeconnexionService.peutRecevoirNotif(eq(userId), anyLong(), eq(Region.CENTRE))).thenReturn(true);

        RoutedNotification routedNotification = createMockRoutedNotification(userId, "notif-abc-123");

        inputTopic.pipeInput(userId, routedNotification, now);

        assertNull(stateStore.get(now + "_notif-abc-123"));
        assertFalse(outputWebTopic.isEmpty(), "La notification aurait dû être envoyée vers le sink.web");

        KeyValue<String, RoutedNotification> outputRecord = outputWebTopic.readKeyValue();
        assertEquals(userId, outputRecord.key);

        // On vérifie simplement que le lien est resté intact, sans chercher à valider un formatage spécifique
        assertEquals("12345", outputRecord.value.getNotification().getContent().getLink());
    }

    @Test
    @DisplayName("Processor : Quand l'utilisateur n'a pas le droit, la notification est retenue dans le StateStore")
    void testProcessNotificationMiseEnAttente() {
        String userId = "user456";
        long now = System.currentTimeMillis();
        Duration delaiSimule = Duration.ofHours(4);
        long deliveryTimeAttendu = now + delaiSimule.toMillis();
        String idNotif = "notif-delayed-999";

        when(ldapRegionService.getRegionByUid(userId)).thenReturn(Region.CENTRE);
        when(ldapRegionService.getListDomaineCentre(userId)).thenReturn(Collections.singletonList("recia.netocentre.fr"));
        when(droitDeconnexionService.peutRecevoirNotif(eq(userId), anyLong(), eq(Region.CENTRE))).thenReturn(false);
        when(droitDeconnexionService.calculDelai(anyLong(), eq(Region.CENTRE))).thenReturn(delaiSimule);

        RoutedNotification routedNotification = createMockRoutedNotification(userId, idNotif);

        inputTopic.pipeInput(userId, routedNotification, now);

        assertTrue(outputWebTopic.isEmpty(), "Aucun message ne doit sortir s'il y a un délai");

        String cleAttendue = String.format("%d_%s", deliveryTimeAttendu, idNotif);
        RoutedNotification storedNotif = stateStore.get(cleAttendue);

        assertNotNull(storedNotif);
        assertEquals(deliveryTimeAttendu, storedNotif.getDeliveryTime());
    }

    @Test
    @DisplayName("Processor : Test du Punctuator (libération automatique après expiration du délai)")
    void testPunctuatorDeclenchement() {
        long timestampLivraison = 100000L;
        String idNotif = "notif-expired";
        String userId = "user789";

        RoutedNotification routedNotification = createMockRoutedNotification(userId, idNotif);
        routedNotification.setDeliveryTime(timestampLivraison);

        String cleStore = String.format("%d_%s", timestampLivraison, idNotif);
        stateStore.put(cleStore, routedNotification);

        testDriver.advanceWallClockTime(Duration.ofMinutes(2));

        assertFalse(outputWebTopic.isEmpty(), "Le punctuator aurait dû libérer la notification");
        assertNull(stateStore.get(cleStore), "Le store doit être purgé après l'envoi");
    }

    private RoutedNotification createMockRoutedNotification(String userId, String notificationId) {
        Content content = new Content();
        content.setTitle("Titre POC");
        content.setMessage("Contenu du message");
        content.setLink("12345");

        EventHeader eventHeader = new EventHeader();
        eventHeader.setEventId("event-xyz");
        eventHeader.setChannels(new ArrayList<>());

        NotificationHeader notificationHeader = new NotificationHeader();
        notificationHeader.setNotificationId(notificationId);
        notificationHeader.setUserId(userId);
        notificationHeader.setEventHeader(eventHeader);

        Notification notification = new Notification();
        notification.setHeader(notificationHeader);
        notification.setContent(content);

        return new RoutedNotification(notification, ROUTED_TOPIC_WEB);
    }

    @Test
    @DisplayName("Processor (Replay) : Quand la notification est rejouée mais que l'utilisateur n'a toujours pas le droit, elle retourne dans le store")
    void testProcessNotificationReplayMiseEnAttente() {
        String userId = "user-replay-1";
        long now = System.currentTimeMillis();
        Duration delaiSimule = Duration.ofHours(2);
        long deliveryTimeAttendu = now + delaiSimule.toMillis();
        String idNotif = "notif-replay-delayed";

        when(ldapRegionService.getRegionByUid(userId)).thenReturn(Region.CENTRE);
        when(ldapRegionService.getListDomaineCentre(userId)).thenReturn(Collections.singletonList("recia.netocentre.fr"));

        long nowReplay = now + Duration.ofMinutes(30).toMillis();
        when(droitDeconnexionService.peutRecevoirNotif(eq(userId), eq(nowReplay), eq(Region.CENTRE))).thenReturn(false);
        when(droitDeconnexionService.calculDelai(eq(now), eq(Region.CENTRE))).thenReturn(delaiSimule);

        RoutedNotification notificationReplay = createMockRoutedNotification(userId, idNotif);
        notificationReplay.setRetryNumber(1);

        inputTopic.pipeInput(userId, notificationReplay, now);

        assertTrue(outputWebTopic.isEmpty());

        String cleAttendue = String.format("%d_%s", deliveryTimeAttendu, idNotif);
        RoutedNotification storedNotif = stateStore.get(cleAttendue);

        assertNotNull(storedNotif, "La notification réémise doit être stockée à nouveau");
        assertEquals(deliveryTimeAttendu, storedNotif.getDeliveryTime());
    }

    @Test
    @DisplayName("Processor (Replay) : Quand la notification est rejouée et que l'utilisateur a le droit, elle est stockée pour rejeu immédiat (nowReplay)")
    void testProcessNotificationReplayAutorise() {
        String userId = "user-replay-2";
        long now = System.currentTimeMillis();
        long nowReplayAttendue = now + Duration.ofMinutes(30).toMillis();
        String idNotif = "notif-replay-ok";

        when(ldapRegionService.getRegionByUid(userId)).thenReturn(Region.CENTRE);
        when(ldapRegionService.getListDomaineCentre(userId)).thenReturn(Collections.singletonList("recia.netocentre.fr"));

        long nowReplay = now + Duration.ofMinutes(30).toMillis();
        when(droitDeconnexionService.peutRecevoirNotif(eq(userId), eq(nowReplay), eq(Region.CENTRE))).thenReturn(true);

        RoutedNotification notificationReplay = createMockRoutedNotification(userId, idNotif);
        notificationReplay.setRetryNumber(2);

        inputTopic.pipeInput(userId, notificationReplay, now);

        String cleAttendue = String.format("%d_%s", nowReplayAttendue, idNotif);
        RoutedNotification storedNotif = stateStore.get(cleAttendue);

        assertNotNull(storedNotif, "La notification doit être placée temporairement dans le store");
        assertEquals(nowReplayAttendue, storedNotif.getDeliveryTime(), "Le deliveryTime doit être calé sur le timestamp de rejeu (+30 min)");
    }

    @Test
    @DisplayName("Processor (Replay) : Quand la notification a atteint le nombre max d'essais (5), elle va au DLT")
    void testProcessNotificationTropDeRejeuxVaAuDLT() {
        String userId = "user-bloque";
        long now = System.currentTimeMillis();
        String idNotif = "notif-broken";

        RoutedNotification notificationTropDeRetries = createMockRoutedNotification(userId, idNotif);
        notificationTropDeRetries.setRetryNumber(5);

        inputTopic.pipeInput(userId, notificationTropDeRetries, now);

        assertFalse(outputDltTopic.isEmpty(), "La notification aurait dû être envoyée vers le DLT");
        assertTrue(outputWebTopic.isEmpty(), "La notification ne doit pas aller dans le topic web");

        KeyValue<String, RoutedNotification> outputRecord = outputDltTopic.readKeyValue();
        assertEquals(userId, outputRecord.key);
        assertEquals(idNotif, outputRecord.value.getNotification().getHeader().getNotificationId());
    }
}