package org.esco.notification.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;

public class EventBuilder {
    private Event event = new Event();

    public Event build() {
        Event event = this.event;
        this.event = new Event();
        return event;
    }

    public EventBuilder priority(EventPriority priority) {
        this.event.getHeader().setPriority(priority);
        return this;
    }

    public EventBuilder title(String title) {
        this.event.getContent().setTitle(title);
        return this;
    }

    public EventBuilder message(String message) {
        this.event.getContent().setMessage(message);
        return this;
    }

    public EventBuilder date(Date date) {
        this.event.getContent().setDate(date);
        return this;
    }

    public EventBuilder scheduledDate(Date date) {
        this.event.getHeader().setScheduledDate(date);
        return this;
    }


    public EventBuilder expiryDate(Date date) {
        this.event.getHeader().setExpiryDate(date);
        return this;
    }


    public EventBuilder property(String property, String value) {
        if (this.event.getContent().getProperties() == null) {
            this.event.getContent().setProperties(new LinkedHashMap<>());
        }
        this.event.getContent().getProperties().put(property, value);
        return this;
    }

    public EventBuilder group(String... uuids) {
        if (this.event.getHeader().getGroupUuids() == null) {
            this.event.getHeader().setGroupUuids(new ArrayList<>());
        }
        this.event.getHeader().getGroupUuids().addAll(Arrays.asList(uuids));
        return this;
    }

    public EventBuilder user(String... uuids) {
        if (this.event.getHeader().getUserUuids() == null) {
            this.event.getHeader().setUserUuids(new ArrayList<>());
        }
        this.event.getHeader().getUserUuids().addAll(Arrays.asList(uuids));
        return this;
    }

    public EventBuilder media(String... medias) {
        if (this.event.getHeader().getMedias() == null) {
            this.event.getHeader().setMedias(new ArrayList<>());
        }
        this.event.getHeader().getMedias().addAll(Arrays.asList(medias));
        return this;
    }

}
