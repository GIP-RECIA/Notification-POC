package org.esco.notification.emission.config;

import org.esco.notification.emission.component.NotificationMessageListener;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    Queue notificationQueue() {
        return new Queue("notification-emission-service", true);
    }

    @Bean
    TopicExchange notificationExchange() {
        return new TopicExchange("notification");
    }

    @Bean
    Binding eventExchangeBinding(
            @Qualifier("notificationQueue") Queue eventQueue,
            @Qualifier("notificationExchange") TopicExchange eventExchange,
            AmqpAdmin admin) {
        Binding binding = BindingBuilder.bind(eventQueue).to(eventExchange).with("#");
        admin.declareBinding(binding);
        return binding;
    }

    @Bean
    TopicExchange emissionExchange() {
        return new TopicExchange("emission");
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    MessageListenerContainer messageListenerContainer(
            ConnectionFactory connectionFactory,
            Queue queue,
            NotificationMessageListener notificationMessageListener
    ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queue.getName());
        container.setMessageListener(notificationMessageListener);
        return container;
    }

}
