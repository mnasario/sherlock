package com.sherlock.game.core.exception;

public class PayloadConvertException extends RuntimeException {

    private static final String ERROR = "Error to convert the object to payload. %s";

    public PayloadConvertException(String message, Throwable cause) {
        super(String.format(ERROR, message), cause);
    }
}
