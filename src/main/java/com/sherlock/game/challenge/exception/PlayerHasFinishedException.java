package com.sherlock.game.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = PlayerHasFinishedException.MESSAGE)
public class PlayerHasFinishedException extends RuntimeException {

    static final String MESSAGE = "Error to process request, player already has finished the game";

    public PlayerHasFinishedException() {
        super(MESSAGE);
    }
}
