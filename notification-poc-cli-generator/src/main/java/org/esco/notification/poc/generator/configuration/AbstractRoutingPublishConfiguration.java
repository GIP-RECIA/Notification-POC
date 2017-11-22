package org.esco.notification.poc.generator.configuration;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.io.IOException;

public abstract class AbstractRoutingPublishConfiguration<T> implements PublishConfiguration<T> {
    protected final String exchange;
    protected final RoutingKeyStrategy<T> routingKeyStrategy;

    public AbstractRoutingPublishConfiguration(String exchange, RoutingKeyStrategy<T> routingKeyStrategy) {
        this.exchange = exchange;
        this.routingKeyStrategy = routingKeyStrategy;
    }

    @Override
    public void publish(Channel channel, AMQP.BasicProperties props, byte[] body, T bean) throws IOException {
        channel.basicPublish(exchange, getRoutingKey(bean), props, body);
    }

    protected String getRoutingKey(T bean) {
        return routingKeyStrategy.getRoutingKey(bean);
    }
}
