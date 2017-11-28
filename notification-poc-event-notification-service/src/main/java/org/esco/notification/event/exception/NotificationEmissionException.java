package org.esco.notification.event.exception;

public class NotificationEmissionException extends EventException {
    public NotificationEmissionException() {
    }

    public NotificationEmissionException(String s) {
        super(s);
    }

    public NotificationEmissionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NotificationEmissionException(Throwable throwable) {
        super(throwable);
    }

    public NotificationEmissionException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
