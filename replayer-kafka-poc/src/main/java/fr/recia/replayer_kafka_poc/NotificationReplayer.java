package fr.recia.replayer_kafka_poc;

import fr.recia.model_kafka_poc.model.RoutedNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class NotificationReplayer {

    private final static String TOPIC_DLT = "notifications.dlt";
    private final static String GROUP_ID = "replayer";
    private final static String REPLAY_HEADER_NAME = "replays";
    private final static int NUM_RETRIES = 5;

    private final KafkaTemplate<String, RoutedNotification> kafkaTemplate;

    public NotificationReplayer(KafkaTemplate<String, RoutedNotification> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private void handle(ConsumerRecord<String, RoutedNotification> record, String topicOut){
        log.debug("Received a new notification to replay : {} ", record.value());
        int replayCount = 1;
        Header replayHeader = record.headers().lastHeader(REPLAY_HEADER_NAME);
        if (replayHeader != null) {
            String valueStr = new String(replayHeader.value(), StandardCharsets.UTF_8);
            replayCount = Integer.parseInt(valueStr) + 1;
        }
        if(replayCount >= NUM_RETRIES){
            log.debug("Notification {} has already been replayed {} times. Putting it to dead letter topic.", record.value(), replayCount);
            ProducerRecord<String, RoutedNotification> newRecord = new ProducerRecord<>(TOPIC_DLT, record.key(), record.value());
            kafkaTemplate.send(newRecord);
        } else {
            log.debug("Notification {} has been replayed {} times. Putting it in original topic {}", record.value(), replayCount, topicOut);
            ProducerRecord<String, RoutedNotification> newRecord = new ProducerRecord<>(topicOut, record.key(), record.value());
            record.headers().forEach(header -> newRecord.headers().add(header));
            newRecord.headers().remove(REPLAY_HEADER_NAME);
            newRecord.headers().add(REPLAY_HEADER_NAME, Integer.toString(replayCount).getBytes(StandardCharsets.UTF_8));
            kafkaTemplate.send(newRecord);
        }
    }

    @KafkaListener(topics = "notifications.replay.web", groupId = GROUP_ID)
    public void consumeWeb(ConsumerRecord<String, RoutedNotification> record) {
        handle(record, "notifications.web");
    }

    @KafkaListener(topics = "notifications.replay.mail", groupId = GROUP_ID)
    public void consumeMail(ConsumerRecord<String, RoutedNotification> record) {
        handle(record, "notifications.mail");
    }

    @KafkaListener(topics = "notifications.replay.push", groupId = GROUP_ID)
    public void consumePush(ConsumerRecord<String, RoutedNotification> record) {
        handle(record, "notifications.push");
    }
}