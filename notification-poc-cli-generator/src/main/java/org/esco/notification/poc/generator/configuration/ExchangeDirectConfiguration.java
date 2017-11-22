package org.esco.notification.poc.generator.configuration;

import com.rabbitmq.client.Channel;

import java.io.IOException;

public class ExchangeDirectConfiguration extends AbstractPublishConfiguration {
    public ExchangeDirectConfiguration() {
        super("", "random-beans");
    }

    @Override
    public void configure(Channel channel) throws IOException {
        channel.queueDeclare(routingKey, true, false, false, null);
    }
}
