package fr.recia.service_example_kafka_poc;

import fr.recia.event_rest_client_kafka_poc.HttpNotificationClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationClientConfig {

    @Bean
    public HttpNotificationClient notificationClient() {
        return new HttpNotificationClient("http://localhost:8179/event/emit", "GROUPER", "jD60tftTJemjfldxdf4d19CQS82XZN9I");
    }
}

