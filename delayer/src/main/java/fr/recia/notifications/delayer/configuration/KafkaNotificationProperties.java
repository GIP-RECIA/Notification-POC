package fr.recia.notifications.delayer.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "kafka-properties")
@Configuration
public class KafkaNotificationProperties {
    String router;
    String replayer;
    String web;
    String mail;
    String push;
    String dlt;
    String sinkWeb;
    String sinkMail;
    String sinkPush;
    String sinkDlt;
    String store;
    String processor;
    String sourceRouter;
    String sourceReplayer;
    int retries;
}
