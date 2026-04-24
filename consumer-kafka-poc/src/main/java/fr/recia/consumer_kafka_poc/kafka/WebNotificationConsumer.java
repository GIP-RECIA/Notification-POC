package fr.recia.consumer_kafka_poc.kafka;

import fr.recia.consumer_kafka_poc.services.RedisNotificationStore;
import fr.recia.model_kafka_poc.model.RoutedNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;

@Component
@Slf4j
public class WebNotificationConsumer {

    private final RedisNotificationStore redisNotificationStore;

    final KafkaTemplate<String, RoutedNotification> kafkaTemplate;

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
            kafkaTemplate.send("notifications.replayer", routedNotification.getNotification().getHeader().getUserId(), routedNotification);
        }
    }
}