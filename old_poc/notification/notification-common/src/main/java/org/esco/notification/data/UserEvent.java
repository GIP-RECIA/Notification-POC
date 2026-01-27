package org.esco.notification.data;

import lombok.Data;

/**
 * An {@link Event} that has been dispatched to a given user.
 */
@Data
public class UserEvent {
    /**
     * Technical data that should not be displayed to end-user
     */
    private UserEventHeader header;

    /**
     * Business data that should be displayed to end-user.
     */
    private EventContent content;
}
