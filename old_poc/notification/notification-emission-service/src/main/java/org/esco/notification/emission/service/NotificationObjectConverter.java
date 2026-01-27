package org.esco.notification.emission.service;

import org.esco.notification.data.Emission;
import org.esco.notification.data.Notification;

import java.util.Date;

/**
 * Convert {@link Notification} to {@link Emission}.
 */
public interface NotificationObjectConverter {
    Emission toEmission(Notification notification, Date date);

    Emission toEmission(Notification notification, Date date, boolean failed, String message);
}
