package fr.recia.notifications.consumer_web.kafka;

import fr.recia.notifications.consumer_web.configuration.KafkaNotificationProperties;
import fr.recia.notifications.consumer_web.services.RedisNotificationStore;
import fr.recia.notifications.model_kafka.model.RoutedNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebNotificationConsumer {

    private final RedisNotificationStore redisNotificationStore;
    private final KafkaTemplate<String, RoutedNotification> kafkaTemplate;
    private final KafkaNotificationProperties kafkaNotificationProperties;
    private final static String TOPIC_IN = "notifications.web";

    public WebNotificationConsumer(RedisNotificationStore redisNotificationStore, KafkaTemplate<String, RoutedNotification> kafkaTemplate, KafkaNotificationProperties kafkaNotificationProperties) {
        this.redisNotificationStore = redisNotificationStore;
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaNotificationProperties = kafkaNotificationProperties;
    }

    @KafkaListener(topics = TOPIC_IN)
    public void consume(RoutedNotification routedNotification) {
        try {
        log.debug("Web notification received : {}", routedNotification);
        redisNotificationStore.save(routedNotification.getNotification());

        }catch (Exception e) {
            log.warn("Unable to process notification {}, forwarding to delayer.", routedNotification);
            log.error("UNexpected Redis error : ", e);
            int retryCount = routedNotification.getRetryNumber();
            routedNotification.setRetryNumber(++retryCount);
            kafkaTemplate.send(kafkaNotificationProperties.getReplayer(), routedNotification.getNotification().getHeader().getUserId(), routedNotification);
        }
    }
}