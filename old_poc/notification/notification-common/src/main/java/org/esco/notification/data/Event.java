package org.esco.notification.data;

import lombok.Data;

/**
 * An initial Event that occurs in the system.
 */
@Data
public class Event {
    /**
     * Technical data that should not be displayed to end-user.
     */
    private EventHeader header = new EventHeader();

    /**
     * Business data that should be displayed to end-user.
     */
    private EventContent content = new EventContent();
}
