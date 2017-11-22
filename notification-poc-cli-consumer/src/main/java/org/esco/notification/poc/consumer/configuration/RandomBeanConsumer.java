package org.esco.notification.poc.consumer.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.esco.notification.beans.RandomBean;
import org.slf4j.Logger;

import java.io.IOException;

public class RandomBeanConsumer extends DefaultConsumer {
    private final ObjectMapper objectMapper;
    private final DeliveryHandler<RandomBean> afterHandleDelivery;

    public RandomBeanConsumer(Channel channel, ObjectMapper objectMapper, Logger log) {
        this(channel, objectMapper, (consumerTag, envelope, properties, body, bean) -> {
            log.debug(String.format("Event %s received for %s (%s)", properties.getType(), bean, properties.getContentType()));
        });
    }

    public RandomBeanConsumer(Channel channel, ObjectMapper objectMapper, DeliveryHandler<RandomBean> afterHandleDelivery) {
        super(channel);
        this.objectMapper = objectMapper;
        this.afterHandleDelivery = afterHandleDelivery;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        RandomBean bean = objectMapper.readValue(body, RandomBean.class);
        this.afterHandleDelivery.deliveryHandler(consumerTag, envelope, properties, body, bean);
    }
}
