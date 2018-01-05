package org.esco.notification.emission.handler.mail;

import org.esco.notification.data.Notification;
import org.esco.notification.emission.exception.NotificationPerformException;
import org.esco.notification.emission.handler.MediaHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Email notification handler.
 */
@Component
public class EMailMediaHandler implements MediaHandler {
    Logger log = LoggerFactory.getLogger(EMailMediaHandler.class);

    @Override
    public String getMediaKey() {
        return "email";
    }

    @Override
    public void performNotification(Notification notification) throws NotificationPerformException {
        // TODO: Send an email, but throw NotificationPerformException if sending fails.
        log.info(notification.toString());
    }
}
