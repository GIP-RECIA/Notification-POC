package fr.recia.routing_kafka_poc.kafka;

import fr.recia.routing_kafka_poc.configuration.KafkaStreamProperties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Autowired
    private KafkaStreamProperties kafkaStreamProperties;

    @Bean
    public KafkaStreamsConfiguration defaultKafkaStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaStreamProperties.getApplicationId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaStreamProperties.getBootstrapServers());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass());
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, kafkaStreamProperties.getReplicationFactor());
        props.put(StreamsConfig.SECURITY_PROTOCOL_CONFIG, kafkaStreamProperties.getSecurityProtocol());
        // TODO : à revoir, qu'est ce qui se passe quand le router redémarre en condition de prod ?
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams/basic-router-" + UUID.randomUUID());
        props.put("sasl.mechanism", kafkaStreamProperties.getSaslMechanism());
        props.put("sasl.jaas.config", kafkaStreamProperties.getJaasConfig());
        return new KafkaStreamsConfiguration(props);
    }
}

