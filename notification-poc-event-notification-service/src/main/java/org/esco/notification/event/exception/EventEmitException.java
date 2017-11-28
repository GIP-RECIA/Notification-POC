package org.esco.notification.event.exception;

public class EventEmitException extends EventException {
    public EventEmitException() {
    }

    public EventEmitException(String s) {
        super(s);
    }

    public EventEmitException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EventEmitException(Throwable throwable) {
        super(throwable);
    }

    public EventEmitException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
