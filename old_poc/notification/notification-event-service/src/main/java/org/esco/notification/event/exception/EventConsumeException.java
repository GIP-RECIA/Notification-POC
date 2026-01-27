package org.esco.notification.event.exception;

public class EventConsumeException extends EventException {
    private final boolean dontRequeue;

    public EventConsumeException(boolean dontRequeue) {
        this.dontRequeue = dontRequeue;
    }

    public EventConsumeException(boolean dontRequeue, String s) {
        super(s);
        this.dontRequeue = dontRequeue;
    }

    public EventConsumeException(boolean dontRequeue, String s, Throwable throwable) {
        super(s, throwable);
        this.dontRequeue = dontRequeue;
    }

    public EventConsumeException(boolean dontRequeue, Throwable throwable) {
        super(throwable);
        this.dontRequeue = dontRequeue;
    }

    public EventConsumeException(boolean dontRequeue, String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
        this.dontRequeue = dontRequeue;
    }

    public EventConsumeException(String s) {
        this(false, s);
    }

    public EventConsumeException(String s, Throwable throwable) {
        this(false, s, throwable);
    }

    public EventConsumeException(Throwable throwable) {
        this(false, throwable);
    }

    public EventConsumeException(String s, Throwable throwable, boolean b, boolean b1) {
        this(false, s, throwable, b, b1);
    }

    public boolean isDontRequeue() {
        return dontRequeue;
    }
}
