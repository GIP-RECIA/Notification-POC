package fr.recia.consumer_kafka_poc.services;

import fr.recia.model_kafka_poc.model.Notification;
import fr.recia.model_kafka_poc.model.StoredNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class RedisNotificationStore {

    private final RedisTemplate<String, StoredNotification> notificationRedisTemplate;
    private final RedisTemplate<String, String> userIndexRedisTemplate;
    private static final int SCAN_COUNT = 500;

    public RedisNotificationStore(RedisTemplate<String, StoredNotification> notificationRedisTemplate,
                                  RedisTemplate<String, String> userIndexRedisTemplate) {
        this.notificationRedisTemplate = notificationRedisTemplate;
        this.userIndexRedisTemplate = userIndexRedisTemplate;
    }

    private String getNotificationKeyForRedis(Notification notification){
        return getNotificationKeyForRedis(notification.getHeader().getNotificationId());
    }

    private String getNotificationKeyForRedis(String notificationId){
        return "notification:"+notificationId;
    }

    private String getUserIndexKeyForRedis(Notification notification){
        return getUserIndexKeyForRedis(notification.getHeader().getUserId());
    }

    private String getUserIndexKeyForRedis(String userId){
        return "user:" + userId + ":notifications";
    }

    public void save(Notification notif) {
        StoredNotification stored = new StoredNotification(notif, false);
        // Stocker la notification en elle-même
        String notifKey = getNotificationKeyForRedis(notif);
        notificationRedisTemplate.opsForValue().set(notifKey, stored, Duration.ofDays(7));
        log.trace("Notification {} added to redis for key {}", notifKey, stored);
        // Stocker le lien user --> ensemble des notifs
        String userIndex = getUserIndexKeyForRedis(notif);
        userIndexRedisTemplate.opsForSet().add(userIndex, notifKey);
        log.trace("Inverted index stored in redis : added {} to set for user {}", notifKey, userIndex);
    }

    public void delete(String userId, String notifId) {
        notificationRedisTemplate.delete(getNotificationKeyForRedis(notifId));
        userIndexRedisTemplate.opsForSet().remove(getUserIndexKeyForRedis(userId), getNotificationKeyForRedis(notifId));
        log.trace("Deleted notification {} and its mappings", notifId);
    }

    public void markAsRead(String notificationId) {
        String notifKey = getNotificationKeyForRedis(notificationId);
        StoredNotification stored = notificationRedisTemplate.opsForValue().get(notifKey);
        if (stored == null) {
            log.warn("No notification found in redis for key {}", notificationId);
            return;
        }
        stored.setRead(true);
        notificationRedisTemplate.opsForValue().set(notifKey, stored, Duration.ofDays(1));
        log.trace("Marked notification {} as read", notificationId);
    }

    public List<StoredNotification> findAllForUser(String userId) {
        log.trace("Getting notifications for user {}", userId);
        String userIndex = getUserIndexKeyForRedis(userId);
        Set<String> notificationIds = userIndexRedisTemplate.opsForSet().members(userIndex);
        if (notificationIds == null || notificationIds.isEmpty()) {
            log.trace("No notifications found for user {}", userId);
            return List.of();
        }
        List<StoredNotification> allNotifs = new ArrayList<>();
        for (String id : notificationIds) {
            StoredNotification stored = notificationRedisTemplate.opsForValue().get(id);
            if (stored != null) {
                allNotifs.add(stored);
            } else {
                // Nettoyage pour les notifications expirées via TTL
                log.trace("Found an expired notification {} in inverted index {}. Clearing it...", id, userIndex);
                userIndexRedisTemplate.opsForSet().remove(userIndex, id);
            }
        }
        allNotifs.sort(Comparator.comparing(n -> n.getNotification().getHeader().getEventHeader().getCreatedAt(), Comparator.reverseOrder()));
        log.trace("Found notification list {} for user {}", allNotifs, userId);
        return allNotifs;
    }

    // Tache qui s'éxécute à 1h30 tous les jours et qui nettoie dans le redis les clés dans les ensembles qui ne sont plus associées à rien
    @Scheduled(cron = "0 30 1 * * *")
    public void cleanupOrphanNotifications() {
        log.info("Launching notification GC for inverted indexes...");
        ScanOptions options = ScanOptions.scanOptions().match("user:*:notifications").count(SCAN_COUNT).build();
        try (Cursor<String> cursor = userIndexRedisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                String userIndexKey = cursor.next();
                cleanupUserIndex(userIndexKey);
            }
        }
    }


    private void cleanupUserIndex(String userIndexKey) {
        Set<String> notifIds = userIndexRedisTemplate.opsForSet().members(userIndexKey);
        List<Object> existsResults = notificationRedisTemplate.executePipelined(
                (RedisCallback<Object>) connection -> {
                    notifIds.forEach(id -> {
                        byte[] key = (id).getBytes(StandardCharsets.UTF_8);
                        connection.keyCommands().exists(key);
                    });
                    return null;
                }
        );

        Iterator<String> idIterator = notifIds.iterator();
        Iterator<Object> existsIterator = existsResults.iterator();
        while (idIterator.hasNext() && existsIterator.hasNext()) {
            String notifId = idIterator.next();
            Boolean exists = (Boolean) existsIterator.next();
            if (!exists) {
                log.trace("Found an expired notification {} in inverted index {}. Clearing it...", notifId, userIndexKey);
                userIndexRedisTemplate.opsForSet().remove(userIndexKey, notifId);
            }
        }
    }
}
