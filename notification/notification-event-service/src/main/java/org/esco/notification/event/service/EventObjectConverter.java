package org.esco.notification.event.service;

import org.esco.notification.data.Event;
import org.esco.notification.data.Notification;
import org.esco.notification.data.UserEvent;

/**
 * Provides convertions for {@link Event}, {@link UserEvent} and {@link Notification}.
 * <p>
 * It may use a configuration to find out which user to notify, and through which medias.
 */
public interface EventObjectConverter {
    UserEvent toUserEvent(Event event, String userUid);

    Notification toNotification(UserEvent userEvent, String media);
}
