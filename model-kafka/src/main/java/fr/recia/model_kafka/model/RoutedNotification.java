package fr.recia.model_kafka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoutedNotification {
    private Notification notification;
    private String routedTopic;
    private int retryNumber = 0;
    private long deliveryTime = 0;

    public RoutedNotification(Notification notification, String routedTopic) {
        this.notification = notification;
        this.routedTopic = routedTopic;
        this.retryNumber = 0;
        this.deliveryTime = 0;
    }
}