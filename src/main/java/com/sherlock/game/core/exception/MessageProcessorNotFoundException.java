package com.sherlock.game.core.exception;

public class MessageProcessorNotFoundException extends RuntimeException {

    static final String MESSAGE = "Message processor not found";

    public MessageProcessorNotFoundException() {
        super(MESSAGE);
    }
}
