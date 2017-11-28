package org.esco.notification.event.service;

import org.esco.notification.data.Event;
import org.esco.notification.data.Notification;
import org.esco.notification.event.exception.EventConsumerException;

import java.util.List;

/**
 * Consume event and produce notification
 */
public interface EventConsumerService {
    void consume(Event event) throws EventConsumerException;
}
