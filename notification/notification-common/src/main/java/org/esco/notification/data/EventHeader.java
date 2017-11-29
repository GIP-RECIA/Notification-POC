package org.esco.notification.data;

import lombok.Data;

import java.util.Date;
import java.util.List;

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
