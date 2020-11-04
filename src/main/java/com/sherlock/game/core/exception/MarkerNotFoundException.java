package com.sherlock.game.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = MarkerNotFoundException.MESSAGE)
public class MarkerNotFoundException extends RuntimeException {

    static final String MESSAGE = "Marker not found";

    public MarkerNotFoundException() {
        super(MESSAGE);
    }
}
