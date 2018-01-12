package org.esco.notification.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Technical header of {@link Notification}.
 *
 * It contains data that should not be displayed to end-user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationHeader {
    private UserEventHeader userEvent;
    private String media;
}
