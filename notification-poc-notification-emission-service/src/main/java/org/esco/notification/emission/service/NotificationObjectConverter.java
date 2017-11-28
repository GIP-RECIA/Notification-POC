package org.esco.notification.emission.service;

import org.esco.notification.data.Emission;
import org.esco.notification.data.Event;
import org.esco.notification.data.Notification;
import org.esco.notification.data.UserEvent;

import java.util.Date;

public interface NotificationObjectConverter {
    Emission toEmission(Notification notification, Date date);

    Emission toEmission(Notification notification, Date date, boolean failed, String message);
}
