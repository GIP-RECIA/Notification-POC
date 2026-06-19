package fr.recia.notifications.delayer.kafka;

import fr.recia.notifications.delayer.configuration.FrequencyDuration;
import fr.recia.notifications.model_kafka_serde.model.RoutedNotificationSerde;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.common.serialization.Serdes;
import fr.recia.notifications.delayer.services.DroitDeconnexionService;
import fr.recia.notifications.delayer.services.LdapRegionService;
import fr.recia.notifications.delayer.services.LdapBypassDroitDeconnexionService;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class PunctuatorTopology {

    private final static String SINK_WEB = "sink.web";
    private final static String SINK_MAIL = "sink.mail";
    private final static String SINK_PUSH = "sink.push";
    private final static String SINK_DLT = "sink.dlt";

    private final static String TOPIC_OUT_WEB = "notifications.web";
    private final static String TOPIC_OUT_MAIL = "notifications.mail";
    private final static String TOPIC_OUT_PUSH = "notifications.push";
    private final static String TOPIC_OUT_DLT = "notifications.dlt";

    private final static String TOPIC_IN_ROUTER = "notifications.router";
    private final static String TOPIC_IN_REPLAYER = "notifications.replayer";

    private final static String STORE = "delayer-store";

    private final static String PROCESSOR_DELAYER = "processor-delayer";

    private final static String SOURCE_ROUTER = "router";
    private final static String SOURCE_REPLAYER = "replayer";


    @Bean
    public KafkaStreams kafkaStreams(Topology topology, KafkaStreamsConfiguration defaultKafkaStreamsConfig) {
        KafkaStreams streams = new KafkaStreams(
                topology,
                defaultKafkaStreamsConfig.asProperties()
        );
        streams.start();
        return streams;
    }

    @Bean
    public Topology topology(DroitDeconnexionService droitDeconnexionService, LdapRegionService ldapRegionService, LdapBypassDroitDeconnexionService ldapBypassDroitDeconnexionService, RoutedNotificationSerde routedNotificationSerde, FrequencyDuration frequencyDuration) {

        Topology topology = new Topology();

        topology.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(STORE),
                        Serdes.String(),
                        routedNotificationSerde
                )
        );

        topology.addSource(
                SOURCE_ROUTER,
                Serdes.String().deserializer(),
                routedNotificationSerde.deserializer(),
                TOPIC_IN_ROUTER
        );

        topology.addSource(
                SOURCE_REPLAYER,
                Serdes.String().deserializer(),
                routedNotificationSerde.deserializer(),
                TOPIC_IN_REPLAYER
        );

        topology.addProcessor(
                PROCESSOR_DELAYER,
                () -> {
                    ProcessorDelayer processor = new ProcessorDelayer(droitDeconnexionService, ldapRegionService, ldapBypassDroitDeconnexionService);
                    processor.setScanFrequency(Duration.ofSeconds(frequencyDuration.getDuration()));
                    return processor;
                },
                SOURCE_ROUTER,
                SOURCE_REPLAYER
        );

        topology.connectProcessorAndStateStores(
                PROCESSOR_DELAYER,
                STORE
        );

        topology.addSink(
                SINK_WEB,
                TOPIC_OUT_WEB,
                Serdes.String().serializer(),
                routedNotificationSerde.serializer(),
                PROCESSOR_DELAYER
        );

        topology.addSink(
                SINK_MAIL,
                TOPIC_OUT_MAIL,
                Serdes.String().serializer(),
                routedNotificationSerde.serializer(),
                PROCESSOR_DELAYER
        );

        topology.addSink(
                SINK_PUSH,
                TOPIC_OUT_PUSH,
                Serdes.String().serializer(),
                routedNotificationSerde.serializer(),
                PROCESSOR_DELAYER
        );

        topology.addSink(
                SINK_DLT,
                TOPIC_OUT_DLT,
                Serdes.String().serializer(),
                routedNotificationSerde.serializer(),
                PROCESSOR_DELAYER
        );

        return topology;
    }
}