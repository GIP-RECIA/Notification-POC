package fr.recia.delayer_poc.services;

import fr.recia.model_kafka_poc.model.RoutedNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.*;
import fr.recia.delayer_poc.*;


@Service
@Slf4j
public class RedisDelayedNotificationStore {

    private final RedisTemplate<String, RoutedNotification> notificationRedisTemplate;
    private final RedisTemplate<String, String> userIndexRedisTemplate;

    private String getNotificationKeyForRedis(String notificationId){
        return "delayed:"+notificationId;
    }

    public RedisDelayedNotificationStore(RedisTemplate<String, RoutedNotification> notificationDelayedRedisTemplate, RedisTemplate<String, String> userIndexRedisTemplate) {
        this.notificationRedisTemplate = notificationDelayedRedisTemplate;
        this.userIndexRedisTemplate = userIndexRedisTemplate;
    }

    public void save(RoutedNotification record, Duration ttl) {
        String notifId = record.getNotification().getHeader().getNotificationId();
        String notifKey = getNotificationKeyForRedis(notifId);
        String mirrorKey = "mirror-delayed:" + notifId;

        notificationRedisTemplate.opsForValue().set(notifKey, record, (ttl));
        log.trace("Notification {} added to redis for key {}", notifKey, record);

        Duration mirrorTtl = ttl.minusHours(1);
        userIndexRedisTemplate.opsForValue().set(mirrorKey, "", mirrorTtl);
        log.trace("Notification mirror {} added to redis for key {}", mirrorKey, record);
    }

}
