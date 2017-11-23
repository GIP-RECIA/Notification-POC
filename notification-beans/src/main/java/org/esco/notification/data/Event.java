package org.esco.notification.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event {
    private final EventType type;
    private EventPriority priority;
    private String title;
    private String message;
    private Date date;
    private Date scheduledDate;
    private Date expiryDate;
    private Map<String, String> properties;

    Event(EventType type) {
        this.type = type;
    }

    public EventPriority getPriority() {
        return priority;
    }

    void setPriority(EventPriority priority) {
        this.priority = priority;
    }

    public EventType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    void setDate(Date date) {
        this.date = date;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    void setProperty(String property, String value) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(property, value);
    }
}
