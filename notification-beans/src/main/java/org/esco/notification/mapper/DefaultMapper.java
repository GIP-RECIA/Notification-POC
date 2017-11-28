package org.esco.notification.mapper;

import org.esco.notification.data.*;

import java.util.Date;

public class DefaultMapper implements Mapper {
    @Override
    public UserEvent toUserEvent(Event event, String userUid) {
        UserEvent userEvent = new UserEvent();
        userEvent.setHeader(new UserEventHeader(event.getHeader(), userUid));
        userEvent.setContent(event.getContent());
        return userEvent;
    }

    @Override
    public Notification toNotification(UserEvent userEvent, String media) {
        Notification notification = new Notification();
        notification.setHeader(new NotificationHeader(userEvent.getHeader(), media));
        notification.setContent(userEvent.getContent());
        return notification;
    }

    @Override
    public Emission toEmission(Notification notification, Date date) {
        return toEmission(notification, date, false, null);
    }

    @Override
    public Emission toEmission(Notification notification, Date date, boolean failed, String message) {
        Emission emission = new Emission();
        emission.setHeader(new EmissionHeader(notification.getHeader(), date, failed, message));
        emission.setContent(notification.getContent());
        return null;
    }
}
