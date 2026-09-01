package fr.recia.notifications.consumer_web.services;

import fr.recia.notifications.consumer_web.configuration.RedisProperties;
import fr.recia.notifications.model_kafka.model.Notification;
import fr.recia.notifications.model_kafka.model.StoredNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private RedisProperties redisProperties;

    public RedisNotificationStore(RedisTemplate<String, StoredNotification> notificationRedisTemplate,
                                  RedisTemplate<String, String> userIndexRedisTemplate,
                                  RedisProperties redisProperties) {
        this.notificationRedisTemplate = notificationRedisTemplate;
        this.userIndexRedisTemplate = userIndexRedisTemplate;
        this.redisProperties = redisProperties;
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
        notificationRedisTemplate.opsForValue().set(notifKey, stored, Duration.ofDays(redisProperties.getTtl()));
        log.trace("Notification {} added to redis for key {}", notifKey, stored);
        // Stocker le lien user --> ensemble des notifs
        String userIndex = getUserIndexKeyForRedis(notif);
        userIndexRedisTemplate.opsForSet().add(userIndex, notifKey);
        log.trace("Inverted index stored in redis : added {} to set for user {}", notifKey, userIndex);
    }

    public void delete(String userId, List<String> notifIds) {
        for (String notifId : notifIds) {
            try {
                String notifKey = getNotificationKeyForRedis(notifId);
                StoredNotification stored = notificationRedisTemplate.opsForValue().get(notifKey);
                if (stored == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification introuvable");
                }
                if (!stored.getNotification().getHeader().getUserId().equals(userId)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès non autorisé à cette notification");
                }
                notificationRedisTemplate.delete(notifKey);
                userIndexRedisTemplate.opsForSet().remove(getUserIndexKeyForRedis(userId), notifKey);
                log.trace("Deleted notification {} and its mappings", notifId);
            } catch (ResponseStatusException e) {
                log.trace("Could not delete notification {} for user {} : {}", notifId, userId, e.getReason());
            }
        }
    }

    public void markAsRead(String userId, List<String> notificationIds) {
        for (String notificationId : notificationIds) {
            try {
                String notifKey = getNotificationKeyForRedis(notificationId);
                StoredNotification stored = notificationRedisTemplate.opsForValue().get(notifKey);
                if (stored == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification introuvable");
                }
                if (!stored.getNotification().getHeader().getUserId().equals(userId)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès non autorisé à cette notification");
                }
                stored.setRead(true);
                notificationRedisTemplate.opsForValue().set(notifKey, stored, Duration.ofDays(1));
                log.trace("Marked notification {} as read", notificationId);
            } catch (ResponseStatusException e) {
                log.trace("Could not mark notification {} as read for user {} : {}", notificationId, userId, e.getReason());
            }
        }
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

    public List<String> notifIdsList(String userId) {
        List<StoredNotification> notifsList = findAllForUser(userId);
        List<String> notifIds = new ArrayList<>();
        for (int i = 0; i < notifsList.size(); i++) {
            notifIds.add(notifsList.get(i).getNotification().getHeader().getNotificationId());
        }
        return notifIds;
    }

    // Tache qui s'éxécute à 1h30 tous les jours et qui nettoie dans le redis les clés dans les ensembles qui ne sont plus associées à rien
    @Scheduled(cron = "0 30 1 * * *")
    public void cleanupOrphanNotifications() {
        log.info("Launching notification GC for inverted indexes...");
        ScanOptions options = ScanOptions.scanOptions().match("user:*:notifications").count(redisProperties.getScanCount()).build();
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