package org.esco.notification.event.component;

import org.esco.notification.data.Event;
import org.esco.notification.event.service.EventConsumerService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventMessageListener implements MessageListener {
    @Autowired
    private EventConsumerService consumerService;

    @Autowired
    private MessageConverter messageConverter;

    @Override
    public void onMessage(Message message) {
        Object event = messageConverter.fromMessage(message);
        consumerService.consume((Event) event);
    }
}
