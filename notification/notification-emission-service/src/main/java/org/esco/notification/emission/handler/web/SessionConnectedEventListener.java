package org.esco.notification.emission.handler.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.security.Principal;

/**
 * When a user connects, register it's UUID and name into SessionService.
 */
@Component
public class SessionConnectedEventListener implements ApplicationListener<SessionConnectedEvent> {
    @Autowired
    private WebMediaUserService sessionService;

    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {
        Principal user = event.getUser();
        if (user instanceof OAuth2Authentication) {
            sessionService.registerUser((OAuth2Authentication) user);
        }
    }
}
