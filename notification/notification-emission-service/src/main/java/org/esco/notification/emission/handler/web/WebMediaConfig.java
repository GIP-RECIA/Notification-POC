package org.esco.notification.emission.handler.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebMediaConfig extends AbstractWebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        /** queue prefix for SUBSCRIPTION (FROM server to CLIENT)  */
        config.enableSimpleBroker("/topic", "/queue", "/notifications");
        /** queue prefix for SENDING messages (FROM client TO server) */
        //config.setApplicationDestinationPrefixes("/notifications");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp")
                .setAllowedOrigins("*");
        //registry.addEndpoint("/notifications").withSockJS();
    }
}
