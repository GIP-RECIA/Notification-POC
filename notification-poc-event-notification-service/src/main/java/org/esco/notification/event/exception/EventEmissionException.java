package org.esco.notification.event.exception;

public class EventEmissionException extends EventException {
    public EventEmissionException() {
    }

    public EventEmissionException(String s) {
        super(s);
    }

    public EventEmissionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EventEmissionException(Throwable throwable) {
        super(throwable);
    }

    public EventEmissionException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
