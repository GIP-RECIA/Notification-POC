package fr.recia.notifications.consumer_push.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "fcm")
@Data
public class FCMProperties {
    private String serviceAccountPath;
    private String fcmUrl;
    private String firebaseAuthUrl;
}
