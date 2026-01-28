package fr.recia.consumer_kafka_poc.controller;

import fr.recia.consumer_kafka_poc.services.RedisNotificationStore;
import fr.recia.model_kafka_poc.model.StoredNotification;
import fr.recia.soffit_java_client.SoffitPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Void> markNotificationAsRead(@RequestParam String notifId) {
        // TODO : check if notif is from principal
        redisNotificationStore.markAsRead(notifId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/delete")
    public ResponseEntity<Void> deleteNotification(@RequestParam String notifId, @AuthenticationPrincipal SoffitPrincipal principal) {
        String userId = principal.getUsername();
        redisNotificationStore.delete(userId, notifId);
        return ResponseEntity.accepted().build();
    }

}
