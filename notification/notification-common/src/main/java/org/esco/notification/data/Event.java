package org.esco.notification.data;

import lombok.Data;

@Data
public class Event {
    private EventHeader header = new EventHeader();
    private EventContent content = new EventContent();
}
