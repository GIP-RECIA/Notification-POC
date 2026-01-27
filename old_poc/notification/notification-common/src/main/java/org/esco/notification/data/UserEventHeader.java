package org.esco.notification.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Technical header for {@link UserEvent}.
 *
 * It contains data that should not be displayed to end-user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEventHeader {
    private EventHeader event;
    private String userUuid;
}
