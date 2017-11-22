package org.esco.notification.poc.generator.configuration;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.io.IOException;

public abstract class AbstractPublishConfiguration<T> implements PublishConfiguration<T> {
    protected final String exchange;
    protected final String routingKey;

    protected AbstractPublishConfiguration(String exchange, String routingKey) {
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public void publish(Channel channel, AMQP.BasicProperties props, byte[] body, T bean) throws IOException {
        channel.basicPublish(exchange, routingKey, props, body);
    }
}
