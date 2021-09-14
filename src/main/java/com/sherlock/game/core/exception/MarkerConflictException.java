package com.sherlock.game.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = MarkerConflictException.MESSAGE)
public class MarkerConflictException extends RuntimeException {

    static final String MESSAGE = "Marker is already exists";

    public MarkerConflictException() {
        super(MESSAGE);
    }
}
