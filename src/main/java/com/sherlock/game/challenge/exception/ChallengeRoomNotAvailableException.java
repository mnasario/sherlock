package com.sherlock.game.challenge.exception;

public class ChallengeRoomNotAvailableException extends RuntimeException {

    static final String MESSAGE = "Challenge room not available. Game has been started.";

    public ChallengeRoomNotAvailableException() {
        super(MESSAGE);
    }
}
