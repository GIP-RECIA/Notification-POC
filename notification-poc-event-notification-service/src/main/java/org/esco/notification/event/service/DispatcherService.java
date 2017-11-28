package org.esco.notification.event.service;

import org.esco.notification.data.Event;
import org.esco.notification.data.Notification;

import java.util.List;

public interface DispatcherService {
    List<Notification> dispatchEvent(Event event);
}
