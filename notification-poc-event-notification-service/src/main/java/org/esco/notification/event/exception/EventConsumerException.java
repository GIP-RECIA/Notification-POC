package org.esco.notification.event.exception;

public class EventConsumerException extends EventException {
    private final boolean dontRequeue;

    public EventConsumerException(boolean dontRequeue) {
        this.dontRequeue = dontRequeue;
    }

    public EventConsumerException(boolean dontRequeue, String s) {
        super(s);
        this.dontRequeue = dontRequeue;
    }

    public EventConsumerException(boolean dontRequeue, String s, Throwable throwable) {
        super(s, throwable);
        this.dontRequeue = dontRequeue;
    }

    public EventConsumerException(boolean dontRequeue, Throwable throwable) {
        super(throwable);
        this.dontRequeue = dontRequeue;
    }

    public EventConsumerException(boolean dontRequeue, String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
        this.dontRequeue = dontRequeue;
    }

    public EventConsumerException(String s) {
        this(false, s);
    }

    public EventConsumerException(String s, Throwable throwable) {
        this(false, s, throwable);
    }

    public EventConsumerException(Throwable throwable) {
        this(false, throwable);
    }

    public EventConsumerException(String s, Throwable throwable, boolean b, boolean b1) {
        this(false, s, throwable, b, b1);
    }

    public boolean isDontRequeue() {
        return dontRequeue;
    }
}
