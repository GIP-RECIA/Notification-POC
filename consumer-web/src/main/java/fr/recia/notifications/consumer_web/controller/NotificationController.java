package fr.recia.notifications.consumer_web.controller;

import fr.recia.notifications.consumer_web.repository.NotificationRepository;
import fr.recia.notifications.model_kafka.model.StoredNotification;
import fr.recia.notifications.soffit_java_client.SoffitPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notif")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<List<StoredNotification>> getAllNotifications(@AuthenticationPrincipal SoffitPrincipal principal) {
        String userId = principal.getUsername();
        List<StoredNotification> notifs = notificationRepository.findAllForUser(userId);
        return ResponseEntity.of(Optional.ofNullable(notifs));
    }

    @GetMapping("/read")
    public ResponseEntity<Void> markNotificationsAsRead(@RequestParam List<String> notifIds, @AuthenticationPrincipal SoffitPrincipal principal) {
        String userId = principal.getUsername();
        notificationRepository.markAsRead(userId, notifIds);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/delete")
    public ResponseEntity<Void> deleteNotifications(@RequestParam List<String> notifIds, @AuthenticationPrincipal SoffitPrincipal principal) {
        String userId = principal.getUsername();
        notificationRepository.delete(userId, notifIds);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/delete-all")
    public ResponseEntity<Void> deleteAllNotifications(@AuthenticationPrincipal SoffitPrincipal principal) {
        String userId = principal.getUsername();
        List<String> notifIds = notificationRepository.notifIdsList(userId);
        notificationRepository.delete(userId, notifIds);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(@AuthenticationPrincipal SoffitPrincipal principal) {
        String userId = principal.getUsername();
        List<String> notifIds = notificationRepository.notifIdsList(userId);
        notificationRepository.markAsRead(userId, notifIds);
        return ResponseEntity.accepted().build();
    }
}