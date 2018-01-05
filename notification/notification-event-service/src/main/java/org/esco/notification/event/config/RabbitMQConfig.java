package org.esco.notification.event.config;

import org.esco.notification.event.component.EventMessageListener;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * RabbitMQ Client configuration.
 */
@Configuration
public class RabbitMQConfig {
    @Bean
    Queue eventQueue() {
        return new Queue("event-notification-service", true);
    }

    @Bean
    TopicExchange eventExchange() {
        TopicExchange exchange = new TopicExchange("event");
        return exchange;
    }

    @Bean
    Binding eventExchangeBinding(
            @Qualifier("eventQueue") Queue eventQueue,
            @Qualifier("eventExchange") TopicExchange eventExchange,
            AmqpAdmin admin) {
        Binding binding = BindingBuilder.bind(eventQueue).to(eventExchange).with("#");
        admin.declareBinding(binding);
        return binding;
    }

    @Bean
    TopicExchange notificationExchange() {
        return new TopicExchange("notification");
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    MessageListenerContainer messageListenerContainer(
            ConnectionFactory connectionFactory,
            Queue queue,
            EventMessageListener eventMessageListener
    ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queue.getName());
        container.setMessageListener(eventMessageListener);
        return container;
    }

}
