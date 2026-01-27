package org.esco.notification.data;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Technical header of {@link Event}.
 *
 * It contains data that should not be displayed to end-user.
 */
@Data
public class EventHeader {
    private String type;
    private EventPriority priority;

    private List<String> groupUuids;
    private List<String> userUuids;
    private List<String> medias;

    private Date scheduledDate;
    private Date expiryDate;
}
