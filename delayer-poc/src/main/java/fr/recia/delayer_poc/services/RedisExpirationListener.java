package fr.recia.delayer_poc.services;

import fr.recia.model_kafka_poc.model.RoutedNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisExpirationListener implements MessageListener{

    private final static String TOPIC_OUT_WEB = "ok.web";
    private final static String TOPIC_OUT_MAIL = "ok.mail";
    private final static String TOPIC_OUT_PUSH = "ok.push";

    private final static String TOPIC_IN_WEB = "notifications.web";
    private final static String TOPIC_IN_MAIL = "notifications.mail";
    private final static String TOPIC_IN_PUSH = "notifications.push";

    private final RedisTemplate<String, RoutedNotification> notificationRedisTemplate;
    private final KafkaTemplate<String, RoutedNotification> kafkaTemplate;

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        String expiredKey = new String(message.getBody());
        log.debug("Clé expirée trouvée: {}", expiredKey);

        if (expiredKey.startsWith("mirror-delayed:")) {

            String notifId = expiredKey.replace("mirror-delayed:", "");
            String notifKey = "delayed:" + notifId;

            RoutedNotification notificationExpire = notificationRedisTemplate.opsForValue().get(notifKey);

            String topicNotif = switch (notificationExpire.getRoutedTopic()) {
                case TOPIC_IN_WEB -> TOPIC_OUT_WEB;
                case TOPIC_IN_MAIL -> TOPIC_OUT_MAIL;
                case TOPIC_IN_PUSH -> TOPIC_OUT_PUSH;
                    default -> {
                        log.warn("Erreur, le topic d'entrée n'a pas été trouvé");
                        yield null;
                    }
                };

            kafkaTemplate.send(topicNotif, notificationExpire.getNotification().getHeader().getUserId(), notificationExpire);
            log.debug("une notif délayée {} a été envoyée vers : {}", notificationExpire, topicNotif);
            notificationRedisTemplate.delete(notifKey);
            log.debug("une notif a été supprimée {}", notifKey);

        }
    }
}
