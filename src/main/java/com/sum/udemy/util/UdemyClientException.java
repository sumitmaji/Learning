package com.sum.udemy.util;

public class UdemyClientException extends RuntimeException {

    private int code;
    private Status status;

    public UdemyClientException(String message) {
        super(message);
    }

    public UdemyClientException(String message, Throwable t) {
        super(message, t);
    }

    public UdemyClientException(Status status) {
        this(status.getMessage(), status.getCode(), status);
    }

    public UdemyClientException(String message, int code, Status status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    public static RuntimeException launderThrowable(Throwable cause) {
        return launderThrowable("An error has occurred.", cause);
    }

    public static RuntimeException launderThrowable(String message, Throwable cause) {
        if (cause instanceof RuntimeException) {
            return ((RuntimeException) cause);
        } else if (cause instanceof Error) {
            throw ((Error) cause);
        } else if (cause instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        throw new UdemyClientException(message, cause);
    }
}
