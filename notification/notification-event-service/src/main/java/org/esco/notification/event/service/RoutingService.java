package org.esco.notification.event.service;

import org.esco.notification.data.Event;
import org.esco.notification.data.UserEvent;

import java.util.List;

public interface RoutingService {
    List<String> getMatchingUsers(Event event);
    List<String> getMatchingMedias(UserEvent userEvent);
}
