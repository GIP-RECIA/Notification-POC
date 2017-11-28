package org.esco.notification.event.service;

import org.esco.notification.data.Event;
import org.esco.notification.data.Notification;
import org.esco.notification.event.exception.NotificationEmissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventConsumerServiceImpl implements EventConsumerService {
    Logger log = LoggerFactory.getLogger(EventConsumerServiceImpl.class);

    @Autowired
    private DispatcherService dispatcherService;

    @Autowired
    private NotificationEmitterService notificationEmitterService;

    @Override
    public void consume(Event event) {
        List<Notification> notifications = dispatcherService.dispatchEvent(event);
        notifications.forEach(notification -> {
            try {
                notificationEmitterService.emit(notification);
            } catch (NotificationEmissionException e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
