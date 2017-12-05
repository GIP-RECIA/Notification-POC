package org.esco.notification.emission.handler.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
public class SessionDisconnectEventListener implements ApplicationListener<SessionDisconnectEvent> {
    @Autowired
    private WebMediaUserService sessionService;


    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        if (user instanceof OAuth2Authentication) {
            sessionService.unregisterUser((OAuth2Authentication)user);
        }
    }
}
