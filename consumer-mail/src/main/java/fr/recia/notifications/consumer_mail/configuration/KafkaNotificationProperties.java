package fr.recia.notifications.consumer_mail.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "kafka-mail-properties")
@Configuration
public class KafkaNotificationProperties {
    String mailFrom;
    String topicReplayer;
}
