package fr.recia.consumer_push_poc;

import fr.recia.model_kafka_poc.model.RoutedNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;

@Component
@Slf4j
public class PushNotificationConsumer {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, ex) -> new TopicPartition("notifications.replay.push", record.partition()));
        return new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 0));
    }

    @KafkaListener(topics = "notifications.push", groupId = "push-consumer")
    public void consume(RoutedNotification routedNotification) {
        log.debug("Notification push reçue : {}", routedNotification);
    }

}

