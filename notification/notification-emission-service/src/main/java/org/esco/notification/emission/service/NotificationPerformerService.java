package org.esco.notification.emission.service;

import org.esco.notification.data.Notification;
import org.esco.notification.emission.exception.NotificationPerformException;

/**
 * Perform notification.
 */
public interface NotificationPerformerService {
    void performNotification(Notification notification) throws NotificationPerformException;
}
