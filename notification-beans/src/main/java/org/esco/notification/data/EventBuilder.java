package org.esco.notification.data;

import java.util.Date;

public class EventBuilder {
    private Event event;

    public EventBuilder(EventType type) {
        this.event = new Event(type);
    }

    public Event build() {
        Event event = this.event;
        this.event = new Event(this.event.getType());
        return event;
    }

    public EventBuilder priority(EventPriority priority) {
        this.event.setPriority(priority);
        return this;
    }

    public Event title(String title) {
        this.event.setTitle(title);
        return this.event;
    }

    public Event message(String message) {
        this.event.setMessage(message);
        return this.event;
    }

    public Event date(Date date) {
        this.event.setDate(date);
        return this.event;
    }

    public Event scheduledDate(Date date) {
        this.event.setScheduledDate(date);
        return this.event;
    }


    public Event expiryDate(Date date) {
        this.event.setExpiryDate(date);
        return this.event;
    }


    public Event property(String property, String value) {
        this.event.setProperty(property, value);
        return this.event;
    }

}
