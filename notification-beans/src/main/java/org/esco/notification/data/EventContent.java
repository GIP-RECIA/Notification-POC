package org.esco.notification.data;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class EventContent {
    private String title;
    private String message;

    private Date date;

    private Map<String, String> properties;
}
