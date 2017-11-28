package org.esco.notification.event.exception;

public class EventException extends RuntimeException {
    public EventException() {
    }

    public EventException(String s) {
        super(s);
    }

    public EventException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EventException(Throwable throwable) {
        super(throwable);
    }

    public EventException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
