package org.esco.notification.emission.service;

import org.esco.notification.data.Emission;
import org.esco.notification.emission.exception.EmissionEmitException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmissionEmitterServiceImpl implements EmissionEmitterService {
    /*
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private TopicExchange emissionExchange;
    */

    @Override
    public void emit(Emission emission) throws EmissionEmitException {
        /*
        try {
            amqpTemplate.convertAndSend(emissionExchange.getName(), "", emission);
        } catch (AmqpException amqpException) {
            throw new EmissionEmitException(amqpException.getMessage(), amqpException);
        }
        */
    }
}
