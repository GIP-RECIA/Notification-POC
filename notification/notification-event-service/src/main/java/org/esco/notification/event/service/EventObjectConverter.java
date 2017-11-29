package org.esco.notification.event.service;

import org.esco.notification.data.*;

public interface EventObjectConverter {
    UserEvent toUserEvent(Event event, String userUid);

    Notification toNotification(UserEvent userEvent, String media);
}
