package org.esco.notification.event.service;

import org.esco.notification.data.Notification;
import org.esco.notification.event.exception.NotificationEmitException;

/**
 * Emit {@link Notification}
 */
public interface NotificationEmitterService {
    void emit(Notification notification) throws NotificationEmitException;
}
