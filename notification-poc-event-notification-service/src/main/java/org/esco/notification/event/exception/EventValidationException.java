package org.esco.notification.event.exception;

public class EventValidationException extends EventException {
    public EventValidationException() {
    }

    public EventValidationException(String s) {
        super(s);
    }

    public EventValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EventValidationException(Throwable throwable) {
        super(throwable);
    }

    public EventValidationException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
