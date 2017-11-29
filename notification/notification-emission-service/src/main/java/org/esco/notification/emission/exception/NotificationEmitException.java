package org.esco.notification.emission.exception;

public class NotificationEmitException extends NotificationException {
    public NotificationEmitException() {
        super();
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

    protected NotificationEmitException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
