package fr.recia.notifications.consumer_web.controller;

import fr.recia.notifications.consumer_web.services.RedisNotificationStore;
import fr.recia.notifications.model_kafka.model.StoredNotification;
import fr.recia.notifications.soffit_java_client.SoffitPrincipal;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notif")
public class NotificationController {

    private final RedisNotificationStore redisNotificationStore;

    public NotificationController(RedisNotificationStore redisNotificationStore) {
        this.redisNotificationStore = redisNotificationStore;
    }

    @GetMapping("/all")
    public ResponseEntity<List<StoredNotification>> getAllNotifications(@AuthenticationPrincipal SoffitPrincipal principal) {
        String userId = principal.getUsername();
        List<StoredNotification> notifs = redisNotificationStore.findAllForUser(userId);
        return ResponseEntity.of(Optional.ofNullable(notifs));
    }

    @GetMapping("/read")
    public ResponseEntity<Void> markNotificationAsRead(@RequestParam String notifId, @AuthenticationPrincipal SoffitPrincipal principal) {
        String userId = principal.getUsername();
        redisNotificationStore.markAsRead(notifId, userId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/delete")
    public ResponseEntity<Void> deleteNotification(@RequestParam String notifId, @AuthenticationPrincipal SoffitPrincipal principal) {
        String userId = principal.getUsername();
        redisNotificationStore.delete(userId, notifId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/delete-all")
    public ResponseEntity<Void> deleteNotifications(@AuthenticationPrincipal SoffitPrincipal principal) {
        String userId = principal.getUsername();
        List<String> notifIds = redisNotificationStore.notifIdsList(userId);
        redisNotificationStore.deleteAll(userId, notifIds);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/read-all")
    public ResponseEntity<Void> markNotificationsAsRead(@AuthenticationPrincipal SoffitPrincipal principal) {
        String userId = principal.getUsername();
        List<String> notifIds = redisNotificationStore.notifIdsList(userId);
        redisNotificationStore.markAllAsRead(userId, notifIds);
        return ResponseEntity.accepted().build();
    }

}
