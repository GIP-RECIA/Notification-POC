package org.esco.notification.emission.service;

import org.esco.notification.data.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumerServiceImpl implements NotificationConsumerService {
    @Autowired
    private NotificationPerformerService notificationPerformerService;

    @Override
    public void consume(Notification notification) {
        notificationPerformerService.performNotification(notification);
    }
}
