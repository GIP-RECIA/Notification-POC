package fr.recia.replayer_kafka_poc;

import fr.recia.model_kafka_poc.model.RoutedNotification;
import fr.recia.replayer_kafka_poc.service.RedisReplayedNotificationStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.time.*;
import java.time.Duration;

@Component
@Slf4j
public class NotificationReplayer {

    private final static String TOPIC_DLT = "notifications.dlt";
    private final static String GROUP_ID = "replayer";
    private final static String REPLAY_HEADER_NAME = "replays";
    private final static int NUM_RETRIES = 5;
    private final static String TOPIC_IN_REPLAYER = "notifications.replayer";

    private final KafkaTemplate<String, RoutedNotification> kafkaTemplate;
    private final RedisReplayedNotificationStore redisReplayedNotificationStore;

    public NotificationReplayer(KafkaTemplate<String, RoutedNotification> kafkaTemplate, RedisTemplate<String, RoutedNotification> redisTemplate, RedisReplayedNotificationStore redisReplayedNotificationStore) {
        this.kafkaTemplate = kafkaTemplate;
        this.redisReplayedNotificationStore = redisReplayedNotificationStore;
    }

    private void handle(ConsumerRecord<String, RoutedNotification> record){
        RoutedNotification notification = record.value();
        log.debug("Received a new notification to replay : {} ", notification);
        int replayCount = record.value().getRetryNumber();

        if(replayCount >= NUM_RETRIES){
            log.debug("Notification {} has already been replayed {} times. Putting it to dead letter topic.", notification, replayCount);
            ProducerRecord<String, RoutedNotification> newRecord = new ProducerRecord<>(TOPIC_DLT, record.key(), notification);
            kafkaTemplate.send(newRecord);
        } else {
            log.debug("Notification {} has been replayed {} times. Putting it in Redis to be replayed ", notification, replayCount);
            notification.setRetryNumber(++replayCount);

            ZonedDateTime maintenant = Instant.ofEpochMilli(record.timestamp()).atZone(ZoneId.of("Europe/Paris"));
            Duration ttl = Duration.between(maintenant, maintenant.plusHours(1));
            redisReplayedNotificationStore.save(notification, ttl);
        }
    }

    @KafkaListener(topics = TOPIC_IN_REPLAYER, groupId = GROUP_ID)
    public void consumeReplay(ConsumerRecord<String, RoutedNotification> record) {
        handle(record);
    }
}