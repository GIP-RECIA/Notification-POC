package fr.recia.delayer.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.model_kafka.model.RoutedNotificationSerde;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaSerdeConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RoutedNotificationSerde routedNotificationSerde(ObjectMapper objectMapper) {
        return new RoutedNotificationSerde(objectMapper);
    }
}
