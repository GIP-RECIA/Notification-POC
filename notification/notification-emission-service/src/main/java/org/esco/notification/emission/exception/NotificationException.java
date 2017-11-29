package org.esco.notification.emission.exception;

public class NotificationException extends RuntimeException {
    public NotificationException() {
        super();
    }

    public NotificationException(String s) {
        super(s);
    }

    public NotificationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NotificationException(Throwable throwable) {
        super(throwable);
    }

    protected NotificationException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
