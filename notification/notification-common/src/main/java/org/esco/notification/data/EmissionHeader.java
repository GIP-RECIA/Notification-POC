package org.esco.notification.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Technical header of {@link Emission}.
 *
 * It contains data that should not be displayed to end-user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmissionHeader {
    private NotificationHeader notification;

    private Date date;
    boolean failed;
    String message;
}
