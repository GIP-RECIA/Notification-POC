package fr.recia.consumer_web.kafka;

import fr.recia.consumer_web.services.RedisNotificationStore;
import fr.recia.model_kafka.model.RoutedNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebNotificationConsumer {

    private final RedisNotificationStore redisNotificationStore;
    private final KafkaTemplate<String, RoutedNotification> kafkaTemplate;

    private final static String TOPIC_OUT_REPLAY = "notifications.replayer";

    public WebNotificationConsumer(RedisNotificationStore redisNotificationStore, KafkaTemplate<String, RoutedNotification> kafkaTemplate) {
        this.redisNotificationStore = redisNotificationStore;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "ok.web", groupId = "web-consumer")
    public void consume(RoutedNotification routedNotification) {
        try {
        log.debug("Notification web ok reçue : {}", routedNotification);
        redisNotificationStore.save(routedNotification.getNotification());

        }catch (Exception e) {
            log.warn("Une notification {} n'a pas pu être traitée, envoie vers le delayer", routedNotification);
            int retryCount = routedNotification.getRetryNumber();
            routedNotification.setRetryNumber(++retryCount);
            kafkaTemplate.send(TOPIC_OUT_REPLAY, routedNotification.getNotification().getHeader().getUserId(), routedNotification);
        }
    }
}