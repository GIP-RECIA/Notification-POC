package fr.recia.delayer_poc.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.model_kafka_poc.model.RoutedNotificationSerde;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.common.serialization.Serdes;
import fr.recia.delayer_poc.services.DroitDeconnexionService;
import fr.recia.delayer_poc.services.LdapRegionService;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.stereotype.Component;

@Component
public class PunctuatorTopology {

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
    public Topology topology(DroitDeconnexionService droitDeconnexionService, LdapRegionService ldapRegionService) {
        Topology topology = new Topology();

        topology.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore("delayer-store"),
                        Serdes.String(),
                        new RoutedNotificationSerde(new ObjectMapper())
                )
        );

        topology.addSource(
                "source",
                Serdes.String().deserializer(),
                new RoutedNotificationSerde(new ObjectMapper()).deserializer(),
                "notifications.router"
        );

        topology.addSource(
                "source2",
                Serdes.String().deserializer(),
                new RoutedNotificationSerde(new ObjectMapper()).deserializer(),
                "notifications.replayer"
        );

        topology.addProcessor(
                "processor-delayer",
                () -> new ProcessorDelayer(droitDeconnexionService, ldapRegionService),
                "source",
                "source2"
        );

        topology.connectProcessorAndStateStores(
                "processor-delayer",
                "delayer-store"
        );

        topology.addSink(
                "sink.web",
                "ok.web",
                Serdes.String().serializer(),
                new RoutedNotificationSerde(new ObjectMapper()).serializer(),
                "processor-delayer"
        );

        topology.addSink(
                "sink.mail",
                "ok.mail",
                Serdes.String().serializer(),
                new RoutedNotificationSerde(new ObjectMapper()).serializer(),
                "processor-delayer"
        );

        topology.addSink(
                "sink.push",
                "ok.push",
                Serdes.String().serializer(),
                new RoutedNotificationSerde(new ObjectMapper()).serializer(),
                "processor-delayer"
        );

        topology.addSink(
                "sink.dlt",
                "notifications.dlt",
                Serdes.String().serializer(),
                new RoutedNotificationSerde(new ObjectMapper()).serializer(),
                "processor-delayer"
        );

        return topology;
    }
}