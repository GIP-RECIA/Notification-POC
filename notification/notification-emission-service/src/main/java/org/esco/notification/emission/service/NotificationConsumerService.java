package org.esco.notification.emission.service;

import org.esco.notification.data.Notification;
import org.esco.notification.emission.exception.NotificationException;

public interface NotificationConsumerService {
    void consume(Notification notification) throws NotificationException;
}
