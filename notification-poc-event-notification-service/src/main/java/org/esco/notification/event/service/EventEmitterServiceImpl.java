package org.esco.notification.event.service;

import org.esco.notification.data.Event;
import org.esco.notification.event.exception.EventEmissionException;
import org.esco.notification.event.exception.EventValidationException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class EventEmitterServiceImpl implements EventEmitterService {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private TopicExchange eventExchange;

    @Override
    public void validate(Event event) throws EventValidationException {
        //TODO: Implement event validation.
    }

    @Override
    public void emit(Event event) throws EventEmissionException {
        try {
            amqpTemplate.convertAndSend(eventExchange.getName(), "", event);
        } catch (AmqpException amqpException) {
            throw new EventEmissionException(amqpException.getMessage(), amqpException);
        }

    }
}
