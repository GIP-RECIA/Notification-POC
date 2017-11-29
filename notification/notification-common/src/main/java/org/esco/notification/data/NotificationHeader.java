package org.esco.notification.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationHeader {
    private UserEventHeader userEvent;
    private String media;
}
