package fr.recia.delayer_poc.kafka;

import fr.recia.delayer_poc.services.RedisDelayedNotificationStore;
import fr.recia.model_kafka_poc.model.RoutedNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import fr.recia.delayer_poc.services.DroitDeconnexionService;

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


    public final static String GROUP_ID = "delayer-consumer";

    //private final RedisDelayedNotificationStore redisDelayedNotificationStore;
    private final KafkaTemplate<String, RoutedNotification> kafkaTemplate;
    private final DroitDeconnexionService droitDeconnexionService;
    private final RedisDelayedNotificationStore redisDelayedNotificationStore;


    @KafkaListener (topics = TOPIC_IN_WEB, groupId = GROUP_ID)
    public void consumeWeb(ConsumerRecord<String, RoutedNotification> record) {
        String userId = record.key();
        RoutedNotification notification = record.value();
        log.trace("notification web {} reçue par {}", notification, GROUP_ID);

        if (droitDeconnexionService.peutRecevoirNotif(userId, record.timestamp(), "centre")) {
            kafkaTemplate.send(TOPIC_OUT_WEB, userId, notification);
            log.trace("notification WEB {} transmise vers {}", notification, TOPIC_OUT_WEB);
        }else {
            redisDelayedNotificationStore.save(notification);
            log.info("notification {} délayée et transférée vers le redis", notification);
        }

    }



    /*@KafkaListener(topics = TOPIC_IN_WEB, groupId = GROUP_ID)
    public void consumeWeb(ConsumerRecord<String, RoutedNotification> record) {
        String userId = record.key();
        RoutedNotification notification = record.value();

        //ZonedDateTime notifHeure = Instant.ofEpochMilli(record.timestamp( )).atZone(ZoneId.of("Europe/Paris"));


        log.info("notification web {} reçue par {}", notification, GROUP_ID);

        if(peutEnvoyer(record.timestamp())){
            kafkaTemplate.send(TOPIC_OUT_WEB, userId, notification);
        }else {
            log.info("notification {} a été délayée vers le redis", notification);
        }

        // kafkaTemplate.send(TOPIC_OUT_WEB, userId, notification);
        // log.trace("notification web {} transmise vers {}", notification, TOPIC_OUT_WEB);
        // redisDelayedNotificationStore.save(routedNotification.getNotification());

        }*/

    @KafkaListener (topics = TOPIC_IN_MAIL, groupId = GROUP_ID)
    public void consumeMail(ConsumerRecord<String, RoutedNotification> record) {
        String userId = record.key();
        RoutedNotification notification = record.value();
        log.trace("notification mail {} reçue par {}", notification, GROUP_ID);
        kafkaTemplate.send(TOPIC_OUT_MAIL, userId, notification);
        log.trace("notification mail {} transmise vers {}", notification, TOPIC_OUT_MAIL);
    }

    @KafkaListener (topics = TOPIC_IN_PUSH, groupId = GROUP_ID)
    public void consumePush(ConsumerRecord<String, RoutedNotification> record) {
        String userId = record.key();
        RoutedNotification notification = record.value();
        log.trace("notification Push {} reçue par {}", notification, GROUP_ID);
        kafkaTemplate.send(TOPIC_OUT_PUSH, userId, notification);
        log.trace("notification push {} transmise vers {}", notification, TOPIC_OUT_PUSH);
    }
}
