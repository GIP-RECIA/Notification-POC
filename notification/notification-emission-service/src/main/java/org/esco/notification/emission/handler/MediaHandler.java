package org.esco.notification.emission.handler;

import org.esco.notification.data.Notification;
import org.esco.notification.data.NotificationHeader;
import org.esco.notification.emission.exception.NotificationPerformException;

/**
 * Handle the performing of {@link Notification} on a particular media.
 */
public interface MediaHandler {
    /**
     * Media will be used when Notification header match this string.
     *
     * @return
     * @see NotificationHeader#getMedia()
     */
    String getMediaKey();

    /**
     * Perform the notification on a particular media.
     *
     * @param notification
     * @throws NotificationPerformException
     */
    void performNotification(Notification notification) throws NotificationPerformException;
}
