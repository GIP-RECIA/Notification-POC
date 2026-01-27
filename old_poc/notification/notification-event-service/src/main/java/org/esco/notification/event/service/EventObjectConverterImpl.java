package org.esco.notification.event.service;

import org.esco.notification.data.*;
import org.springframework.stereotype.Service;

@Service
public class EventObjectConverterImpl implements EventObjectConverter {

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
}
