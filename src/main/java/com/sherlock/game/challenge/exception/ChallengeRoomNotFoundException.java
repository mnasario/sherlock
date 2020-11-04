package com.sherlock.game.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = ChallengeRoomNotFoundException.MESSAGE)
public class ChallengeRoomNotFoundException extends RuntimeException {

    static final String MESSAGE = "Challenge room not found";

    public ChallengeRoomNotFoundException() {
        super(MESSAGE);
    }
}
