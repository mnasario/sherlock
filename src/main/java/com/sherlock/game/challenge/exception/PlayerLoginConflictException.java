package com.sherlock.game.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = PlayerLoginConflictException.MESSAGE)
public class PlayerLoginConflictException extends RuntimeException {

    static final String MESSAGE = "Error to login, player name is already exist to room";

    public PlayerLoginConflictException() {
        super(MESSAGE);
    }
}
