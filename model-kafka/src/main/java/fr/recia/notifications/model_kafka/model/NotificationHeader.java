package fr.recia.notifications.model_kafka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationHeader {
    private String notificationId;
    private String userId;
    private EventHeader eventHeader;
}
