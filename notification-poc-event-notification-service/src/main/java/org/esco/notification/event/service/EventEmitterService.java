package org.esco.notification.event.service;

import org.esco.notification.data.Event;
import org.esco.notification.event.exception.EventEmissionException;
import org.esco.notification.event.exception.EventValidationException;

/**
 * Validate and emit event
 */
public interface EventEmitterService {

    void validate(Event event) throws EventValidationException;

    void emit(Event event) throws EventEmissionException;
}
