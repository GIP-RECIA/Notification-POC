package org.esco.notification.event.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQInit {
    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        // do whatever you need here
    }
}
