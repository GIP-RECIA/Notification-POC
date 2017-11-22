package org.esco.notification.poc.consumer.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import org.slf4j.Logger;

import java.io.IOException;

public class ExchangeDirectConfiguration implements ConsumeConfiguration {
    private final String queue;

    public ExchangeDirectConfiguration() {
        this("random-beans");
    }

    public ExchangeDirectConfiguration(String queue) {
        this.queue = queue;
    }

    @Override
    public void configure(Channel channel, ObjectMapper objectMapper, Logger log) throws IOException {
        channel.queueDeclare(queue, true, false, false, null);

        Consumer consumer = new RandomBeanConsumer(channel, objectMapper, log);

        channel.basicConsume(queue, true, consumer);
    }
}
