package org.esco.notification.emission.handler.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * Configuration of Spring STOMP Relay to underlying RabbitMQ.
 *
 * This acts as a STOMP proxy and allow browser connections.
 */
@Configuration
@EnableWebSocketMessageBroker
@Order(50)
public class WebMediaConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Autowired
    private RabbitProperties rabbitProperties;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        /** queue prefix for SUBSCRIPTION (FROM server to CLIENT)  */
        config.enableStompBrokerRelay("/queue")
                .setRelayHost(rabbitProperties.getHost()).setRelayPort(61613) // TODO: Should be configurable
                .setClientLogin(rabbitProperties.getUsername()).setClientPasscode(rabbitProperties.getPassword())
                .setSystemLogin(rabbitProperties.getUsername()).setSystemPasscode(rabbitProperties.getPassword());
        /** queue prefix for SENDING messages (FROM client TO server) */
        config.setUserDestinationPrefix("/user");
        //config.setApplicationDestinationPrefixes("/user", "/web-media");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp")
                .setAllowedOrigins("*");
        //registry.addEndpoint("/notification").withSockJS();
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        super.configureClientOutboundChannel(registration);
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .nullDestMatcher().authenticated()
                .simpDestMatchers("/user", "/user/**", "/queue", "/queue/**").authenticated()
                .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
