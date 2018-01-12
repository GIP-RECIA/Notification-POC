package org.esco.notification.data;

import lombok.Data;

/**
 * An Emission trace attempts of effective user notification through a given media, ie. sending of a mail.
 * It may haved failed.
 */
@Data
public class Emission {
    /**
     * Technical data that should not be displayed to end-user.
     */
    private EmissionHeader header;

    /**
     * Business data that should be displayed to end-user.
     */
    private EventContent content;
}
