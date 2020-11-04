package com.sherlock.game.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = ChallengeSummaryNotFoundException.MESSAGE)
public class ChallengeSummaryNotFoundException extends RuntimeException {

    static final String MESSAGE = "Summary not found";

    public ChallengeSummaryNotFoundException() {
        super(MESSAGE);
    }
}
