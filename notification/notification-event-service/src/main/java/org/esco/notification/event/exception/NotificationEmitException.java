package org.esco.notification.event.exception;

public class NotificationEmitException extends EventException {
    public NotificationEmitException() {
    }

    public NotificationEmitException(String s) {
        super(s);
    }

    public NotificationEmitException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NotificationEmitException(Throwable throwable) {
        super(throwable);
    }

    public NotificationEmitException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
