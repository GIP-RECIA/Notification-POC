package fr.recia.notifications.preferences_api.service;

import fr.recia.notifications.model_kafka.model.UserPreferences;
import fr.recia.notifications.preferences_api.configuration.KafkaNotificationProperties;
import lombok.extern.slf4j.Slf4j;
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
    private final KafkaNotificationProperties kafkaNotificationProperties;

    // Possible de récupérer une StreamsBuilderFactoryBean car on a initialisé le Bean defaultKafkaStreamsConfig + annotation @EnableKafkaStreams
    public PreferencesQueryService(StreamsBuilderFactoryBean factoryBean, KafkaTemplate<String, UserPreferences> kafkaTemplate, KafkaNotificationProperties kafkaNotificationProperties) {
        this.factoryBean = factoryBean;
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaNotificationProperties = kafkaNotificationProperties;
    }

    public void postPreferences(String userId, UserPreferences preferences){
        kafkaTemplate.send(kafkaNotificationProperties.getTopic(), userId, preferences);
        log.info("New preferences {} set for user {}", preferences, userId);
    }

    // Service qui récupère depuis le store la valeur associée à la clé (le userId)
    public UserPreferences getPreferences(String userId) {
        if (store == null) {
            KafkaStreams streams = factoryBean.getKafkaStreams();
            if (streams == null) {
                throw new IllegalStateException("Kafka streams isn't accessible at this time.");
            } else {
                this.store = streams.store(StoreQueryParameters.fromNameAndType(kafkaNotificationProperties.getStore(), QueryableStoreTypes.keyValueStore()));
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
