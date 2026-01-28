package fr.recia.preferences_api_poc.service;

import fr.recia.model_kafka_poc.model.UserPreferences;
import fr.recia.preferences_api_poc.kafka.KafkaStreamsConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PreferencesQueryService {

    private final StreamsBuilderFactoryBean factoryBean;
    private ReadOnlyKeyValueStore<String, UserPreferences> store;
    private final KafkaTemplate<String, UserPreferences> kafkaTemplate;

    // Possible de récupérer une StreamsBuilderFactoryBean car on a initialisé le Bean defaultKafkaStreamsConfig + annotation @EnableKafkaStreams
    public PreferencesQueryService(StreamsBuilderFactoryBean factoryBean, KafkaTemplate<String, UserPreferences> kafkaTemplate) {
        this.factoryBean = factoryBean;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void postPreferences(String userId, UserPreferences preferences){
        kafkaTemplate.send(KafkaStreamsConfig.TOPIC_NAME, userId, preferences);
        log.info("New preferences {} set for user {}", preferences, userId);
    }

    // Service qui récupère depuis le store la valeur associée à la clé (le userId)
    public UserPreferences getPreferences(String userId) {
        if (store == null) {
            KafkaStreams streams = factoryBean.getKafkaStreams();
            if (streams == null) {
                throw new IllegalStateException("Kafka streams isn't accessible at this time.");
            } else {
                this.store = streams.store(StoreQueryParameters.fromNameAndType(KafkaStreamsConfig.STORE_NAME, QueryableStoreTypes.keyValueStore()));
            }
        }
        try {
            UserPreferences userPreferences = store.get(userId);
            log.trace("Got preferences {} from store for user {}", userPreferences, userId);
            return userPreferences;
        } catch (InvalidStateStoreException e){
            throw new RuntimeException("API is still starting...", e);
        }
    }
}
