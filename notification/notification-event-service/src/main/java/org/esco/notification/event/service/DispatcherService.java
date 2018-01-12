package org.esco.notification.event.service;

import org.esco.notification.data.Event;
import org.esco.notification.data.Notification;

import java.util.List;

/**
 * Dispatch an {@link Event} to a list of {@link Notification}.
 * <p>
 * It may use a configuration to find out which user to notify, and through which medias.
 */
public interface DispatcherService {
    List<Notification> dispatchEvent(Event event);
}
