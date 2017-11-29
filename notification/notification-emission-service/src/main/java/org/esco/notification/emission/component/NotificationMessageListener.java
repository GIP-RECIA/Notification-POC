package org.esco.notification.emission.component;

import org.esco.notification.data.Notification;
import org.esco.notification.emission.service.NotificationConsumerService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageListener implements MessageListener {
    @Autowired
    private NotificationConsumerService consumerService;

    @Autowired
    private MessageConverter messageConverter;

    @Override
    public void onMessage(Message message) {
        Object event = messageConverter.fromMessage(message);
        consumerService.consume((Notification) event);
    }
}
