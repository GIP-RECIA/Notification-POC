package org.esco.notification.emission.service;

import org.esco.notification.data.Notification;
import org.esco.notification.emission.exception.NotificationPerformException;

/**
 * Perform effective {@link Notification}.
 */
public interface NotificationPerformerService {
    void performNotification(Notification notification) throws NotificationPerformException;
}
