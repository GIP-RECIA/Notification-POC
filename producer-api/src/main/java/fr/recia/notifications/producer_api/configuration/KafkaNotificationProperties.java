package fr.recia.notifications.producer_api.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "kafka-topic")
@Configuration
public class KafkaNotificationProperties {
    String topicEvent;
}
