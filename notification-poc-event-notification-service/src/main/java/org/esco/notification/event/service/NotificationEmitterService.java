package org.esco.notification.event.service;

import org.esco.notification.data.Notification;
import org.esco.notification.event.exception.NotificationEmissionException;

/**
 * Validate and emit notification
 */
public interface NotificationEmitterService {
    void emit(Notification notification) throws NotificationEmissionException;
}
