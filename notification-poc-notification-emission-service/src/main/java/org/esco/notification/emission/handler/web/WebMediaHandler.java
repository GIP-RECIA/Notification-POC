package org.esco.notification.emission.handler.web;

import org.esco.notification.data.Notification;
import org.esco.notification.emission.exception.NotificationPerformException;
import org.esco.notification.emission.handler.MediaHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WebMediaHandler implements MediaHandler {
    Logger log = LoggerFactory.getLogger(WebMediaHandler.class);

    @Override
    public String getMediaKey() {
        return "web";
    }

    @Override
    public void performNotification(Notification notification) throws NotificationPerformException {
        log.info(notification.toString());
    }
}
