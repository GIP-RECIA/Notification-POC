package fr.recia.replayer_kafka_poc.service;

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
public class RedisReplayerExpirationListener implements MessageListener {

    private final static String TOPIC_OUT_ROUTER = "notifications.router";

    private final RedisTemplate<String, RoutedNotification> notificationRedisTemplate;
    private final KafkaTemplate<String, RoutedNotification> kafkaTemplate;


    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        String expiredKey = new String(message.getBody());
        log.debug("Clé éxpirée trouvée");

        if (expiredKey.startsWith("mirror-replayed:")) {

            String notifId = expiredKey.replace("mirror-replayed:", "");
            String notifKey = "replayed:" + notifId;

            RoutedNotification notificationExpire = notificationRedisTemplate.opsForValue().get(notifKey);

            kafkaTemplate.send(TOPIC_OUT_ROUTER, notificationExpire.getNotification().getHeader().getUserId(), notificationExpire);
            log.debug("une notif à rejouer {} a été envoyée vers : {}", notificationExpire, TOPIC_OUT_ROUTER);
            notificationRedisTemplate.delete(notifKey);
            log.debug("une notif a été supprimée {}", notifKey);
        }
    }
}
