package org.esco.notification.data;

import lombok.Data;

/**
 * A notification encapsulates content of an event to be displayed to user trough a given media.
 */
@Data
public class Notification {
    /**
     * Technical data that should not be displayed to end-user
     */
    private NotificationHeader header;

    /**
     * Business data that should be displayed to end-user.
     */
    private EventContent content;
}
