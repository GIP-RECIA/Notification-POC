package fr.recia.delayer_poc.kafka;

import fr.recia.delayer_poc.services.DroitDeconnexionService;
import fr.recia.delayer_poc.services.RedisDelayedNotificationStore;
import fr.recia.model_kafka_poc.model.RoutedNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.time.Duration;


@RequiredArgsConstructor
@Component
@Slf4j
public class NotificationDelayer {

    private final static String TOPIC_OUT_WEB = "ok.web";
    private final static String TOPIC_OUT_MAIL = "ok.mail";
    private final static String TOPIC_OUT_PUSH = "ok.push";

    private final static String TOPIC_IN_WEB = "notifications.web";
    private final static String TOPIC_IN_MAIL = "notifications.mail";
    private final static String TOPIC_IN_PUSH = "notifications.push";
    private final static String TOPIC_IN_ROUTER = "notifications.router";

    public final static String GROUP_ID = "delayer-consumer";

    private final KafkaTemplate<String, RoutedNotification> kafkaTemplate;
    private final DroitDeconnexionService droitDeconnexionService;
    private final RedisDelayedNotificationStore redisDelayedNotificationStore;


    @KafkaListener (topics = TOPIC_IN_ROUTER, groupId = GROUP_ID)
    public void consumeDelay(ConsumerRecord<String, RoutedNotification> record) {
        String userId = record.key();
        RoutedNotification notification = record.value();
        log.trace("notification routée {} reçue par {}", notification, GROUP_ID);
        String topicOut = switch (record.value().getRoutedTopic()) {
            case TOPIC_IN_WEB -> TOPIC_OUT_WEB;
            case TOPIC_IN_MAIL -> TOPIC_OUT_MAIL;
            case TOPIC_IN_PUSH -> TOPIC_OUT_PUSH;
            default -> {
                log.warn("Erreur, le topic d'entrée n'a pas été trouvé");
                yield null;
            }
        };

        if (!droitDeconnexionService.peutRecevoirNotif(userId, record.timestamp(), "centre")) {

            Duration ttl = droitDeconnexionService.calculDelai(record.timestamp(), "centre");

            redisDelayedNotificationStore.save(notification, ttl.plusHours(1));
            log.info("notification Web {} délayée et transférée vers le redis", notification);

        }else {
            kafkaTemplate.send(topicOut, userId, notification);
            log.trace("notification {}, qui est une {} a été transmise vers {}", notification, notification.getRoutedTopic(), topicOut);
        }
    }
/*
    @KafkaListener (topics = TOPIC_IN_WEB, groupId = GROUP_ID)
    public void consumeWeb(ConsumerRecord<String, RoutedNotification> record) {
        String userId = record.key();
        RoutedNotification notification = record.value();
        log.trace("notification Web {} reçue par {}", notification, GROUP_ID);

        if (!droitDeconnexionService.peutRecevoirNotif(userId, record.timestamp(), "centre")) {

            Duration ttl = droitDeconnexionService.calculDelai(record.timestamp(), "centre");

            redisDelayedNotificationStore.save(notification, ttl.plusHours(1));
            log.info("notification Web {} délayée et transférée vers le redis", notification);

        }else {
            kafkaTemplate.send(TOPIC_OUT_WEB, userId, notification);
            log.trace("notification Web {} transmise vers {}", notification, TOPIC_OUT_WEB);
        }
    }

    @KafkaListener (topics = TOPIC_IN_MAIL, groupId = GROUP_ID)
    public void consumeMail(ConsumerRecord<String, RoutedNotification> record) {
        String userId = record.key();
        RoutedNotification notification = record.value();
        log.trace("notification Mail {} reçue par {}", notification, GROUP_ID);

        if (!droitDeconnexionService.peutRecevoirNotif(userId, record.timestamp(), "centre")) {

            Duration ttl = droitDeconnexionService.calculDelai(record.timestamp(), "centre");

            redisDelayedNotificationStore.save(notification, ttl.plusHours(1));
            log.info("notification Mail {} délayée et transférée vers le redis", notification);

        }else {
            kafkaTemplate.send(TOPIC_OUT_MAIL, userId, notification);
            log.trace("notification Mail {} transmise vers {}", notification, TOPIC_IN_MAIL);
        }
    }

    @KafkaListener (topics = TOPIC_IN_PUSH, groupId = GROUP_ID)
    public void consumePush(ConsumerRecord<String, RoutedNotification> record) {
        String userId = record.key();
        RoutedNotification notification = record.value();
        log.trace("notification Push {} reçue par {}", notification, GROUP_ID);

        if (!droitDeconnexionService.peutRecevoirNotif(userId, record.timestamp(), "centre")) {

            Duration ttl = droitDeconnexionService.calculDelai(record.timestamp(), "centre");

            redisDelayedNotificationStore.save(notification, ttl.plusHours(1));
            log.info("notification Push {} délayée et transférée vers le redis", notification);

        }else {
            kafkaTemplate.send(TOPIC_OUT_PUSH, userId, notification);
            log.trace("notification Push {} transmise vers {}", notification, TOPIC_OUT_PUSH);
        }
    }*/
}
