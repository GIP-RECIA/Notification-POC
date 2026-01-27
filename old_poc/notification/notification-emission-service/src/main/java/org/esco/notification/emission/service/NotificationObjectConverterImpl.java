package org.esco.notification.emission.service;

import org.esco.notification.data.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class NotificationObjectConverterImpl implements NotificationObjectConverter {
    @Override
    public Emission toEmission(Notification notification, Date date) {
        return toEmission(notification, date, false, null);
    }

    @Override
    public Emission toEmission(Notification notification, Date date, boolean failed, String message) {
        Emission emission = new Emission();
        emission.setHeader(new EmissionHeader(notification.getHeader(), date, failed, message));
        emission.setContent(notification.getContent());
        return emission;
    }
}
