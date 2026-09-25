package fr.recia.notifications.consumer_web.services;

import fr.recia.notifications.consumer_web.repository.MongoNotificationDocument;
import fr.recia.notifications.consumer_web.repository.NotificationRepository;
import fr.recia.notifications.model_kafka.model.StoredNotification;
import fr.recia.notifications.model_kafka.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
@ConditionalOnProperty(prefix = "notification", name = "storage", havingValue = "mongodb")
@Slf4j
public class MongoNotificationStore implements NotificationRepository {
    private final MongoTemplate mongoTemplate;

    public MongoNotificationStore(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(Notification notification) {
        String notifId = notification.getHeader().getNotificationId();
        String userId = notification.getHeader().getUserId();
        MongoNotificationDocument document = new MongoNotificationDocument(notifId,
                userId, notification, false
        );

        mongoTemplate.save(document);

        log.info("Notification {} added to MongoDB for user {}", notifId, userId);

    }

    @Override
    public void delete(String userId, List<String> notifIds) {
        for (String notifId : notifIds) {
            try {
                MongoNotificationDocument document = mongoTemplate.findById(notifId, MongoNotificationDocument.class);
                if (document == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification introuvable");
                }

                if (!document.getUserId().equals(userId)){
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès non autorisé à cette notification");
                }

                mongoTemplate.remove(document);
                log.trace("Deleted notification {} and its mappings", notifId);
            } catch (ResponseStatusException e) {
                log.trace("Could not delete notification {} for user {} : {}", notifId, userId, e.getReason());
            }
        }
    }

    @Override
    public void markAsRead(String userId, List<String> notificationIds) {
        for (String notificationId : notificationIds) {
            try {
                MongoNotificationDocument document = mongoTemplate.findById(notificationId, MongoNotificationDocument.class);

                if (document == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification introuvable");
                }

                if (!document.getUserId().equals(userId)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accés non autorisé à cette notification");
                }

                document.setRead(true);
                mongoTemplate.save(document);
                log.trace("Marked notification {} as read", notificationId);
            } catch (ResponseStatusException e) {
                log.trace("Could not mark notification {} as read for user {} : {}", notificationId, userId, e.getReason());
            }
        }
    }

    @Override
    public List<StoredNotification> findAllForUser(String userId){
        Query query = new Query(Criteria.where("userId").is(userId));

        List<MongoNotificationDocument> documents = mongoTemplate.find(query, MongoNotificationDocument.class);
        List<StoredNotification> notifications = new ArrayList<>();

        for (MongoNotificationDocument document : documents) {
            notifications.add(new StoredNotification(document.getNotification(), document.isRead()));
        }

        notifications.sort(Comparator.comparing(notification -> notification.getNotification().getHeader().getEventHeader().getCreatedAt(), Comparator.reverseOrder()));

        return notifications;
    }

    @Override
    public List<String> notifIdsList(String userId) {
        List<StoredNotification> notifsList = findAllForUser(userId);
        List<String> notifIds = new ArrayList<>();

        for (StoredNotification notification : notifsList) {
            notifIds.add(notification.getNotification().getHeader().getNotificationId());
        }
        return notifIds;
    }
}
