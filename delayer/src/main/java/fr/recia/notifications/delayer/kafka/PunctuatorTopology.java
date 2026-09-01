package fr.recia.notifications.delayer.kafka;

import fr.recia.notifications.delayer.configuration.FrequencyDuration;
import fr.recia.notifications.delayer.configuration.KafkaNotificationProperties;
import fr.recia.notifications.model_kafka_serde.model.RoutedNotificationSerde;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class PunctuatorTopology {

    private final KafkaNotificationProperties kafkaNotificationProperties;

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
                        Stores.persistentKeyValueStore(kafkaNotificationProperties.getStore()),
                        Serdes.String(),
                        routedNotificationSerde
                )
        );

        topology.addSource(
                kafkaNotificationProperties.getSourceRouter(),
                Serdes.String().deserializer(),
                routedNotificationSerde.deserializer(),
                kafkaNotificationProperties.getRouter()
        );

        topology.addSource(
                kafkaNotificationProperties.getSourceReplayer(),
                Serdes.String().deserializer(),
                routedNotificationSerde.deserializer(),
                kafkaNotificationProperties.getReplayer()
        );

        topology.addProcessor(
                kafkaNotificationProperties.getProcessor(),
                () -> {
                    ProcessorDelayer processor = new ProcessorDelayer(droitDeconnexionService, ldapRegionService, ldapBypassDroitDeconnexionService);
                    processor.setScanFrequency(Duration.ofSeconds(frequencyDuration.getDuration()));
                    processor.setKafkaNotificationProperties(kafkaNotificationProperties);
                    return processor;
                },
                kafkaNotificationProperties.getSourceRouter(),
                kafkaNotificationProperties.getSourceReplayer()
        );

        topology.connectProcessorAndStateStores(
                kafkaNotificationProperties.getProcessor(),
                kafkaNotificationProperties.getStore()
        );

        topology.addSink(
                kafkaNotificationProperties.getSinkWeb(),
                kafkaNotificationProperties.getWeb(),
                Serdes.String().serializer(),
                routedNotificationSerde.serializer(),
                kafkaNotificationProperties.getProcessor()
        );

        topology.addSink(
                kafkaNotificationProperties.getSinkMail(),
                kafkaNotificationProperties.getMail(),
                Serdes.String().serializer(),
                routedNotificationSerde.serializer(),
                kafkaNotificationProperties.getProcessor()
        );

        topology.addSink(
                kafkaNotificationProperties.getSinkPush(),
                kafkaNotificationProperties.getPush(),
                Serdes.String().serializer(),
                routedNotificationSerde.serializer(),
                kafkaNotificationProperties.getProcessor()
        );

        topology.addSink(
                kafkaNotificationProperties.getSinkDlt(),
                kafkaNotificationProperties.getDlt(),
                Serdes.String().serializer(),
                routedNotificationSerde.serializer(),
                kafkaNotificationProperties.getProcessor()
        );

        return topology;
    }
}