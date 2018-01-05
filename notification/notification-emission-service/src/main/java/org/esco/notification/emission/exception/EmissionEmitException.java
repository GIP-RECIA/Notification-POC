package org.esco.notification.emission.exception;

/**
 * Exception thrown when emission fails to be emitted.
 */
public class EmissionEmitException extends NotificationException {
    public EmissionEmitException() {
    }

    public EmissionEmitException(String s) {
        super(s);
    }

    public EmissionEmitException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EmissionEmitException(Throwable throwable) {
        super(throwable);
    }

    public EmissionEmitException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
