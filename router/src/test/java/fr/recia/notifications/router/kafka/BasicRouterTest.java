package fr.recia.notifications.router.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.notifications.model_kafka.model.*;
import fr.recia.notifications.router.configuration.KafkaNotificationProperties;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BasicRouterTest {

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, Notification> inputEventsTopic;
    private TestInputTopic<String, UserPreferences> inputPreferencesTopic;
    private TestOutputTopic<String, RoutedNotification> outputTopic;
    private KafkaNotificationProperties kafkaNotificationProperties;

    private final String TOPIC_EVENTS = "notifications.events.expanded";
    private final String TOPIC_PREFS = "notifications.user.preferences";
    private final String TOPIC_OUT = "notifications.router";

    @BeforeEach
    void setUp() {
        kafkaNotificationProperties = new KafkaNotificationProperties();
        kafkaNotificationProperties.setTopicExpanded(TOPIC_EVENTS);
        kafkaNotificationProperties.setTopicPrefs(TOPIC_PREFS);
        kafkaNotificationProperties.setTopicRouter(TOPIC_OUT);

        BasicRouter router = new BasicRouter(kafkaNotificationProperties);

        ObjectMapper mapper = router.objectMapper();

        Serde<Notification> notificationSerde = router.notificationSerde(mapper);
        Serde<UserPreferences> prefsSerde = router.prefsSerde(mapper);
        Serde<RoutedNotification> routedNotificationSerde = router.routedNotificationSerde(mapper);

        StreamsBuilder builder = new StreamsBuilder();
        router.basicRouting(builder, notificationSerde, prefsSerde, routedNotificationSerde);

        Topology topology = builder.build();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-router");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.put(
                StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
                Serdes.String().getClass().getName()
        );

        testDriver = new TopologyTestDriver(topology, props);

        inputEventsTopic = testDriver.createInputTopic(
                TOPIC_EVENTS,
                Serdes.String().serializer(),
                notificationSerde.serializer()
        );

        inputPreferencesTopic = testDriver.createInputTopic(
                TOPIC_PREFS,
                Serdes.String().serializer(),
                prefsSerde.serializer()
        );

        outputTopic = testDriver.createOutputTopic(
                TOPIC_OUT,
                Serdes.String().deserializer(),
                routedNotificationSerde.deserializer()
        );
    }

    @AfterEach
    void tearDown() {
        if (testDriver != null) {
            testDriver.close();
        }
    }

    @Test
    @DisplayName("Cas 1 : Aucune préférence utilisateur -> Comportement par défaut (WEB)")
    void testRoutingNoPreferences() {
        String userId = "user1";
        // La notification propose WEB et MAIL
        Notification notif = createNotification(userId, "SERVICE_A", Priority.NORMAL, Set.of(Channel.WEB, Channel.MAIL));

        // On n'envoie aucune préférence dans le topic inputPreferencesTopic
        inputEventsTopic.pipeInput(userId, notif);

        assertFalse(outputTopic.isEmpty(), "Un message doit être émis");

        List<KeyValue<String, RoutedNotification>> records = outputTopic.readKeyValuesToList();
        assertEquals(1, records.size(), "Un seul channel doit être résolu par défaut");

        RoutedNotification routed = records.get(0).value;
        assertEquals("notifications.web", routed.getRoutedTopic(), "Le fallback par défaut doit être WEB");
    }

    @Test
    @DisplayName("Cas 2 : Préférences globales uniquement")
    void testRoutingGlobalPreferences() {
        String userId = "user2";
        Notification notif = createNotification(userId, "SERVICE_B", Priority.NORMAL, Set.of(Channel.WEB, Channel.MAIL, Channel.PUSH));

        // L'utilisateur veut WEB et PUSH en global, pas de configuration spécifique pour SERVICE_B
        UserPreferences prefs = new UserPreferences();
        prefs.setGlobal(createChannelPrefs(true, false, true)); // WEB, PUSH

        inputPreferencesTopic.pipeInput(userId, prefs);
        inputEventsTopic.pipeInput(userId, notif);

        List<KeyValue<String, RoutedNotification>> records = outputTopic.readKeyValuesToList();
        assertEquals(2, records.size(), "Deux messages doivent être émis (WEB et PUSH)");

        List<String> expectedTopics = Arrays.asList("notifications.web", "notifications.push");
        assertTrue(expectedTopics.contains(records.get(0).value.getRoutedTopic()));
        assertTrue(expectedTopics.contains(records.get(1).value.getRoutedTopic()));
    }

    @Test
    @DisplayName("Cas 3 : Service désactivé -> Aucune notification envoyée")
    void testRoutingServiceDisabled() {
        String userId = "user3";
        Notification notif = createNotification(userId, "SERVICE_C", Priority.NORMAL, Set.of(Channel.WEB));

        UserPreferences prefs = new UserPreferences();
        prefs.setGlobal(createChannelPrefs(true, true, true));

        ServicePreferences servicePrefs = new ServicePreferences();
        servicePrefs.setEnabled(false); // Service désactivé !

        Map<String, ServicePreferences> servicesMap = new HashMap<>();
        servicesMap.put("SERVICE_C", servicePrefs);
        prefs.setServices(servicesMap);

        inputPreferencesTopic.pipeInput(userId, prefs);
        inputEventsTopic.pipeInput(userId, notif);

        assertTrue(outputTopic.isEmpty(), "Aucune notification ne doit sortir car le service est désactivé");
    }

    @Test
    @DisplayName("Cas 4 : Surcharge par service avec priorité")
    void testRoutingServiceOverride() {
        String userId = "user4";
        // Notification Haute priorité
        Notification notif = createNotification(userId, "SERVICE_D", Priority.HIGH, Set.of(Channel.WEB, Channel.MAIL, Channel.PUSH));

        UserPreferences prefs = new UserPreferences();
        prefs.setGlobal(createChannelPrefs(true, false, false)); // Global : WEB uniquement

        ServicePreferences servicePrefs = new ServicePreferences();
        servicePrefs.setEnabled(true);
        servicePrefs.setOverride(true); // Active la surcharge

        // Surcharge pour la priorité HIGH : veut du MAIL et du PUSH
        Map<Priority, ChannelPreferences> priorities = new HashMap<>();
        priorities.put(Priority.HIGH, createChannelPrefs(false, true, true));
        servicePrefs.setPriorities(priorities);

        Map<String, ServicePreferences> servicesMap = new HashMap<>();
        servicesMap.put("SERVICE_D", servicePrefs);
        prefs.setServices(servicesMap);

        inputPreferencesTopic.pipeInput(userId, prefs);
        inputEventsTopic.pipeInput(userId, notif);

        List<KeyValue<String, RoutedNotification>> records = outputTopic.readKeyValuesToList();
        assertEquals(2, records.size());

        // On vérifie que la surcharge a bien remplacé le "WEB" global par "MAIL" et "PUSH"
        List<String> outputTopics = records.stream().map(kv -> kv.value.getRoutedTopic()).toList();
        assertTrue(outputTopics.contains("notifications.mail"));
        assertTrue(outputTopics.contains("notifications.push"));
        assertFalse(outputTopics.contains("notifications.web"));
    }

    @Test
    @DisplayName("Cas 5 : Vérification de l'intersection des canaux (Service vs Event)")
    void testRoutingChannelIntersection() {
        String userId = "user5";
        // L'événement NE SUPPORTE QUE LE WEB
        Notification notif = createNotification(userId, "SERVICE_E", Priority.NORMAL, Set.of(Channel.WEB));

        // Mais l'utilisateur a configuré qu'il voulait absolument tout (WEB, MAIL, PUSH)
        UserPreferences prefs = new UserPreferences();
        prefs.setGlobal(createChannelPrefs(true, true, true));

        inputPreferencesTopic.pipeInput(userId, prefs);
        inputEventsTopic.pipeInput(userId, notif);

        List<KeyValue<String, RoutedNotification>> records = outputTopic.readKeyValuesToList();

        // L'intersection doit bloquer MAIL et PUSH car l'événement ne les propose pas
        assertEquals(1, records.size());
        assertEquals("notifications.web", records.get(0).value.getRoutedTopic());
    }

    // --- Méthodes utilitaires pour bouchonner les données ---

    private Notification createNotification(String userId, String service, Priority priority, Set<Channel> availableChannels) {
        EventHeader eventHeader = new EventHeader();
        eventHeader.setService(service);
        eventHeader.setPriority(priority);
        eventHeader.setChannels(new ArrayList<>(availableChannels));

        NotificationHeader header = new NotificationHeader();
        header.setUserId(userId);
        header.setEventHeader(eventHeader);

        Notification notification = new Notification();
        notification.setHeader(header);

        return notification;
    }

    private ChannelPreferences createChannelPrefs(boolean ws, boolean mail, boolean push) {
        ChannelPreferences prefs = new ChannelPreferences();
        prefs.setWs(ws);
        prefs.setMail(mail);
        prefs.setPush(push);
        return prefs;
    }
}