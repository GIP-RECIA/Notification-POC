package org.esco.notification.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmissionHeader {
    private NotificationHeader notification;

    private Date date;
    boolean failed;
    String message;
}
