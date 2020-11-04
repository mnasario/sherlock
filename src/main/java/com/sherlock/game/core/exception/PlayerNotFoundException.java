package com.sherlock.game.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = PlayerNotFoundException.MESSAGE)
public class PlayerNotFoundException extends RuntimeException {

    static final String MESSAGE = "Player not found";

    public PlayerNotFoundException() {
        super(MESSAGE);
    }
}
