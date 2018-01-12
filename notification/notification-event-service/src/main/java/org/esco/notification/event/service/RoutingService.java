package org.esco.notification.event.service;

import org.esco.notification.data.Event;
import org.esco.notification.data.UserEvent;

import java.util.List;

/**
 * Map {@link Event} to a list of user uuids, and {@link UserEvent} to a list of media.
 * <p>
 * It may use a configuration to find out which user to notify, and through which medias.
 */
public interface RoutingService {
    List<String> getMatchingUsers(Event event);

    List<String> getMatchingMedias(UserEvent userEvent);
}
