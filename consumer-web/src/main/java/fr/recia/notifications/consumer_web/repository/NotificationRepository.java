package fr.recia.notifications.consumer_web.repository;

import fr.recia.notifications.model_kafka.model.StoredNotification;
import fr.recia.notifications.model_kafka.model.Notification;

import java.util.List;

public interface NotificationRepository {
    void save(Notification notification);
    void delete(String userId, List<String> notificationIds);
    void markAsRead(String userId, List<String> notificationIds);
    List<StoredNotification> findAllForUser(String userId);
    List<String> notifIdsList(String userId);
}