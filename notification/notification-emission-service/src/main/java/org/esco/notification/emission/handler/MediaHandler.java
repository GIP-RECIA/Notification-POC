package org.esco.notification.emission.handler;

import org.esco.notification.data.Notification;
import org.esco.notification.emission.exception.NotificationPerformException;

public interface MediaHandler {
    String getMediaKey();

    void performNotification(Notification notification) throws NotificationPerformException;
}
