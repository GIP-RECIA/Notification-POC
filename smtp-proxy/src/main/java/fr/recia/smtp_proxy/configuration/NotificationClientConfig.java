package fr.recia.smtp_proxy.configuration;

import fr.recia.event_rest_client_kafka.HttpNotificationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationClientConfig {
    @Value("${notification-api.url}")
    private String apiUrl;

    @Value("${notification-api.service-name}")
    private String serviceName;

    @Value("${notification-api.api-key}")
    private String apiKey;

    @Bean
    public HttpNotificationClient notificationClient() {
        return new HttpNotificationClient(apiUrl, serviceName, apiKey);
    }
}