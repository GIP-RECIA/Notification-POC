package org.esco.notification.data;

import lombok.Data;

@Data
public class UserEvent {
    private UserEventHeader header;
    private EventContent content;
}
