package org.esco.notification.event.service;

import org.esco.notification.data.Event;
import org.esco.notification.event.exception.EventException;

/**
 * Consume {@link Event}
 */
public interface EventConsumerService {
    void consume(Event event) throws EventException;
}
