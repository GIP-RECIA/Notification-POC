package org.esco.notification.data;

import lombok.Data;

@Data
public class Notification {
    private NotificationHeader header;
    private EventContent content;
}
