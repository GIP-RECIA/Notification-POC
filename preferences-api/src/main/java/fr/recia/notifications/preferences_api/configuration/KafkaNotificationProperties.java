package fr.recia.notifications.preferences_api.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "kafka-properties")
@Configuration
public class KafkaNotificationProperties {
    String store;
    String topic;
}
