package org.esco.notification.event.service;

import org.esco.notification.data.Event;
import org.esco.notification.event.exception.EventEmitException;
import org.esco.notification.event.exception.EventValidateException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventEmitterServiceImpl implements EventEmitterService {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private TopicExchange eventExchange;

    @Override
    public void validate(Event event) throws EventValidateException {
        //TODO: Implement event validation.
    }

    @Override
    public void emit(Event event) throws EventEmitException {
        try {
            amqpTemplate.convertAndSend(eventExchange.getName(), "", event);
        } catch (AmqpException amqpException) {
            throw new EventEmitException(amqpException.getMessage(), amqpException);
        }

    }
}
