package org.esco.notification.poc.generator.configuration;

import com.rabbitmq.client.Channel;

import java.io.IOException;

public class ExchangeFanoutConfiguration<T> extends AbstractPublishConfiguration<T> {
    public ExchangeFanoutConfiguration() {
        super("random-beans-fanout", "");
    }

    @Override
    public void configure(Channel channel) throws IOException {
        channel.exchangeDeclare(exchange, "fanout");
    }
}
