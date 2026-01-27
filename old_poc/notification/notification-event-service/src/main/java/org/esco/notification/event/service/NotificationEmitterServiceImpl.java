package org.esco.notification.event.service;

import org.esco.notification.data.Notification;
import org.esco.notification.event.exception.NotificationEmitException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationEmitterServiceImpl implements NotificationEmitterService {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private TopicExchange notificationExchange;

    @Override
    public void emit(Notification notification) throws NotificationEmitException {
        try {
            amqpTemplate.convertAndSend(notificationExchange.getName(), "", notification);
        } catch (AmqpException amqpException) {
            throw new NotificationEmitException(amqpException.getMessage(), amqpException);
        }

    }
}
