package org.esco.notification.randombeans.configuration;

import com.rabbitmq.client.Channel;

import java.io.IOException;

public class ExchangeTopicConfiguration<T> extends AbstractRoutingPublishConfiguration<T> {
    public ExchangeTopicConfiguration(RoutingKeyStrategy<T> routingKeyStrategy) {
        super("event", routingKeyStrategy);
    }

    @Override
    public void configure(Channel channel) throws IOException {
        channel.exchangeDeclare(exchange, "topic", true);
    }

}
