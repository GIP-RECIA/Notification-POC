package org.esco.notification.emission.handler.web;

import org.esco.notification.data.Notification;
import org.esco.notification.emission.exception.NotificationPerformException;
import org.esco.notification.emission.handler.MediaHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

@Component
public class WebMediaHandler implements MediaHandler {
    Logger log = LoggerFactory.getLogger(WebMediaHandler.class);

    @Autowired
    private SimpMessagingTemplate brokerMessagingTemplate;

    @Autowired
    private WebMediaUserService userService;

    @Override
    public String getMediaKey() {
        return "web";
    }

    @Override
    public void performNotification(Notification notification) throws NotificationPerformException {
        log.info(notification.toString());

        OAuth2Authentication user = userService.getUserByUuid(notification.getHeader().getUserEvent().getUserUuid());
        if (user == null) {
            user = userService.getUserByName(notification.getHeader().getUserEvent().getUserUuid());
        }

        if (user != null) {
            brokerMessagingTemplate.convertAndSendToUser(user.getName(), "/queue/notification", notification);
        }
    }
}
