package org.esco.notification.poc.generator.configuration;

import com.rabbitmq.client.Channel;

import java.io.IOException;

public class ExchangeRoutingConfiguration<T> extends AbstractRoutingPublishConfiguration<T> {
    public ExchangeRoutingConfiguration(RoutingKeyStrategy<T> routingKeyStrategy) {
        super("random-beans-routing", routingKeyStrategy);
    }

    @Override
    public void configure(Channel channel) throws IOException {
        channel.exchangeDeclare(exchange, "direct");
    }

}
