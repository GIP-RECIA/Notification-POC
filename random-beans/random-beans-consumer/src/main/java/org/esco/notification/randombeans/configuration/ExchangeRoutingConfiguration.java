package org.esco.notification.randombeans.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.slf4j.Logger;

import java.io.IOException;

public class ExchangeRoutingConfiguration implements ConsumeConfiguration {
    private final String exchangeName;

    public ExchangeRoutingConfiguration() {
        this("random-beans-routing");
    }

    public ExchangeRoutingConfiguration(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    @Override
    public void configure(Channel channel, ObjectMapper objectMapper, Logger log) throws IOException {
        String shortTitleQueue = channel.queueDeclare().getQueue();
        channel.queueBind(shortTitleQueue, this.exchangeName, "short-title");

        String longTitleQueue = channel.queueDeclare().getQueue();
        channel.queueBind(longTitleQueue, this.exchangeName, "long-title");

        Consumer shortTitleConsumer = new RandomBeanConsumer(channel, objectMapper, (consumerTag, envelope, properties, body, bean) -> {
            log.debug(String.format("Event %s received for %s (%s). Routing Key: short-title, Title: %s", properties.getType(), bean, properties.getContentType(), bean.getTitle()));
        });

        channel.basicConsume(shortTitleQueue, true, shortTitleConsumer);

        Consumer longTitleConsumer = new RandomBeanConsumer(channel, objectMapper, (consumerTag, envelope, properties, body, bean) -> {
            log.debug(String.format("Event %s received for %s (%s). Routing Key: long-title, Title: %s", properties.getType(), bean, properties.getContentType(), bean.getTitle()));
        });

        channel.basicConsume(longTitleQueue, true, longTitleConsumer);

    }
}
