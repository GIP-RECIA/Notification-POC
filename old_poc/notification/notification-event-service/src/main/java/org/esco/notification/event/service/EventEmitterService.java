package org.esco.notification.event.service;

import org.esco.notification.data.Event;
import org.esco.notification.event.exception.EventEmitException;
import org.esco.notification.event.exception.EventValidateException;

/**
 * Validate and emit {@link Event}
 */
public interface EventEmitterService {

    void validate(Event event) throws EventValidateException;

    void emit(Event event) throws EventEmitException;
}
