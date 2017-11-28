package org.esco.notification.mapper;

import org.esco.notification.data.Emission;
import org.esco.notification.data.Event;
import org.esco.notification.data.Notification;
import org.esco.notification.data.UserEvent;

import java.util.Date;


public interface Mapper {
    UserEvent toUserEvent(Event event, String userUid);

    Notification toNotification(UserEvent userEvent, String media);

    Emission toEmission(Notification notification, Date date);

    Emission toEmission(Notification notification, Date date, boolean failed, String message);
}
