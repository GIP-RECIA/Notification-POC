package org.esco.notification.randombeans.configuration;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.io.IOException;

public interface PublishConfiguration<T> {
    void configure(Channel channel) throws IOException;
    void publish(Channel channel, AMQP.BasicProperties props, byte[] body, T bean) throws IOException;
}
