package org.esco.notification.poc.consumer.configuration;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

public interface DeliveryHandler<T> {
    void deliveryHandler(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body, T bean);
}
