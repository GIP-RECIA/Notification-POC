package fr.recia.consumer_push_poc.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.consumer_push_poc.configuration.KafkaStreamProperties;
import fr.recia.model_kafka_poc.model.DeviceTokenSet;
import fr.recia.model_kafka_poc.model.DeviceTokenSetSerde;
import fr.recia.model_kafka_poc.model.UserPreferencesSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@EnableKafkaStreams
@Configuration
public class KafkaStreamsConfig {

    public static final String STORE_NAME = "push-tokens-store";
    public static final String TOPIC_NAME = "push.tokens";

    @Autowired
    private KafkaStreamProperties kafkaStreamProperties;

    @Bean
    public GlobalKTable<String, DeviceTokenSet> preferencesTable(StreamsBuilder builder, ObjectMapper objectMapper) {
        // Création d'une KTable avec son store associé pour pouvoir accéder au topic des tokens
        DeviceTokenSetSerde deviceTokenSetSerde = new DeviceTokenSetSerde(objectMapper);
        return builder.globalTable(TOPIC_NAME, Consumed.with(Serdes.String(), deviceTokenSetSerde),
                Materialized.<String, DeviceTokenSet, KeyValueStore<Bytes, byte[]>>as(STORE_NAME)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(deviceTokenSetSerde)
        );
    }

    @Bean(name = "defaultKafkaStreamsConfig")
    public KafkaStreamsConfiguration defaultKafkaStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaStreamProperties.getApplicationId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaStreamProperties.getBootstrapServers());
        // TODO : peu importe les types qu'on met ici ça à l'air de fonctionner, c'est étrange
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass());
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, kafkaStreamProperties.getReplicationFactor());
        // TODO : Qu'est ce qui se passe quand le router redémarre en condition de prod ?
        // -> c'est côté client que c'est stocké donc tant qu'on en a qu'un par machine donc on pourra enlever la ligne
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams/push-tokens-" + UUID.randomUUID());
        props.put(StreamsConfig.SECURITY_PROTOCOL_CONFIG, kafkaStreamProperties.getSecurityProtocol());
        props.put("sasl.mechanism", kafkaStreamProperties.getSaslMechanism());
        props.put("sasl.jaas.config", kafkaStreamProperties.getJaasConfig());
        return new KafkaStreamsConfiguration(props);
    }
}