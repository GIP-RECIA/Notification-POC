package org.esco.notification.emission.service;

import org.esco.notification.data.*;

import java.util.Date;

public interface NotificationObjectConverter {
    Emission toEmission(Notification notification, Date date);

    Emission toEmission(Notification notification, Date date, boolean failed, String message);
}
