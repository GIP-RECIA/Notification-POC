package org.esco.notification.poc.consumer.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import org.slf4j.Logger;

import java.io.IOException;

public class ExchangeFanoutConfiguration implements ConsumeConfiguration {
    private final String exchangeName;

    public ExchangeFanoutConfiguration() {
        this("random-beans-fanout");
    }

    public ExchangeFanoutConfiguration(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    @Override
    public void configure(Channel channel, ObjectMapper objectMapper, Logger log) throws IOException {
        String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue, this.exchangeName, "");

        Consumer consumer = new RandomBeanConsumer(channel, objectMapper, log);

        channel.basicConsume(queue, true, consumer);
    }
}
