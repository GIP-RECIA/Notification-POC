package org.esco.notification.emission.exception;

public class NotificationPerformException extends NotificationException {
    public NotificationPerformException() {
    }

    public NotificationPerformException(String s) {
        super(s);
    }

    public NotificationPerformException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NotificationPerformException(Throwable throwable) {
        super(throwable);
    }

    public NotificationPerformException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
