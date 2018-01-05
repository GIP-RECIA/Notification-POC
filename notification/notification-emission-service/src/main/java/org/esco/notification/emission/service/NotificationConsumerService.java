package org.esco.notification.emission.service;

import org.esco.notification.data.Notification;
import org.esco.notification.emission.exception.NotificationException;

/**
 * Consume notification comming from RabbitMQ listener.
 */
public interface NotificationConsumerService {
    void consume(Notification notification) throws NotificationException;
}
