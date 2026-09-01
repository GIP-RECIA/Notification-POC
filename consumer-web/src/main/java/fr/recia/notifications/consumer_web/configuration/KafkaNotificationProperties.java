package fr.recia.notifications.consumer_web.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "kafka-topic")
@Configuration
public class KafkaNotificationProperties {
    String web;
    String replayer;
}
