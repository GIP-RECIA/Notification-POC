package org.esco.notification.event.exception;

public class EventValidateException extends EventException {
    public EventValidateException() {
    }

    public EventValidateException(String s) {
        super(s);
    }

    public EventValidateException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EventValidateException(Throwable throwable) {
        super(throwable);
    }

    public EventValidateException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
