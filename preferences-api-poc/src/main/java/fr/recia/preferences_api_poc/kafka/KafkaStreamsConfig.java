package fr.recia.preferences_api_poc.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.model_kafka_poc.model.UserPreferences;
import fr.recia.model_kafka_poc.model.UserPreferencesSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EnableKafkaStreams
@Configuration
public class KafkaStreamsConfig {

    public static final String STORE_NAME = "user-prefs-store-v2";
    public static final String TOPIC_NAME = "user.preferences-v2";
    public static final String APPLICATION_ID = "preferences-api-v2";

    @Bean
    public GlobalKTable<String, UserPreferences> preferencesTable(StreamsBuilder builder, ObjectMapper objectMapper) {
        // Création d'une KTable avec son store associé pour pouvoir accéder au topic des préférences utilisateur
        UserPreferencesSerde prefsSerde = new UserPreferencesSerde(objectMapper);
        return builder.globalTable(TOPIC_NAME, Consumed.with(Serdes.String(), prefsSerde),
                Materialized.<String, UserPreferences, KeyValueStore<Bytes, byte[]>>as(STORE_NAME)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(prefsSerde)
        );
    }

    @Bean(name = "defaultKafkaStreamsConfig")
    public KafkaStreamsConfiguration defaultKafkaStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092,localhost:49092");
        // TODO : peu importe les types qu'on met ici ça à l'air de fonctionner, c'est étrange
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass());
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 3);
        // TODO : Qu'est ce qui se passe quand le router redémarre en condition de prod ?
        // -> c'est côté client que c'est stocké donc tant qu'on en a qu'un par machine donc on pourra enlever la ligne
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams/basic-router-" + UUID.randomUUID());
        props.put(StreamsConfig.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"preferences-streams\" password=\"wJ2ngZuYh1vRZ642\";");
        return new KafkaStreamsConfiguration(props);
    }
}
