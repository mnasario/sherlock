package com.sherlock.game.core.domain.message;

import lombok.Data;

@Data
public class Envelop {

    private Type type;
    private Subject subject;
    private String payload;
}