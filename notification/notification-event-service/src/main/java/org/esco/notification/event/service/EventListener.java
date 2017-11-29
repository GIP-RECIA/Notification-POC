package org.esco.notification.event.service;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class EventListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        //TODO
    }
}
