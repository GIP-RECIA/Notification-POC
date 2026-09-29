package fr.recia.notifications.consumer_web.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.recia.notifications.model_kafka.model.Notification;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notifications")
public class MongoNotificationDocument {
    @Id
    private String id;

    private String userId;
    private Notification notification;
    private boolean read;
}
