package org.esco.notification.event.service;

import org.esco.notification.data.Event;
import org.esco.notification.event.exception.EventException;

/**
 * Consume event and produce notification
 */
public interface EventConsumerService {
    void consume(Event event) throws EventException;
}
