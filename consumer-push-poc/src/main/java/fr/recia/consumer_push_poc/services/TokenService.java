package fr.recia.consumer_push_poc.services;

import fr.recia.consumer_push_poc.kafka.KafkaStreamsConfig;
import fr.recia.model_kafka_poc.model.DeviceTokenSet;
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
public class TokenService {

    private final StreamsBuilderFactoryBean factoryBean;
    private ReadOnlyKeyValueStore<String, DeviceTokenSet> store;
    private final KafkaTemplate<String, DeviceTokenSet> kafkaTemplate;

    // Possible de récupérer une StreamsBuilderFactoryBean car on a initialisé le Bean defaultKafkaStreamsConfig + annotation @EnableKafkaStreams
    public TokenService(StreamsBuilderFactoryBean factoryBean, KafkaTemplate<String, DeviceTokenSet> kafkaTemplate) {
        this.factoryBean = factoryBean;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void saveToken(String userId, String token){
        DeviceTokenSet tokens = getTokens(userId);
        if(tokens == null){
            tokens = new DeviceTokenSet();
        }
        tokens.add(token);
        kafkaTemplate.send(KafkaStreamsConfig.TOPIC_NAME, userId, tokens);
        log.info("New token {} set for user {}", token, userId);
    }

    public void removeToken(String userId, String token){
        log.info("Removing token {} for user {}", token, userId);
        DeviceTokenSet tokens = getTokens(userId);
        tokens.remove(token);
        kafkaTemplate.send(KafkaStreamsConfig.TOPIC_NAME, userId, tokens);
    }

    public DeviceTokenSet getTokens(String userId) {
        if (store == null) {
            KafkaStreams streams = factoryBean.getKafkaStreams();
            if (streams == null) {
                throw new IllegalStateException("Kafka streams isn't accessible at this time.");
            } else {
                this.store = streams.store(StoreQueryParameters.fromNameAndType(KafkaStreamsConfig.STORE_NAME, QueryableStoreTypes.keyValueStore()));
            }
        }
        try {
            DeviceTokenSet tokens = store.get(userId);
            log.trace("Got preferences {} from store for user {}", tokens, userId);
            return tokens;
        } catch (InvalidStateStoreException e){
            throw new RuntimeException("API is still starting...", e);
        }
    }
}
