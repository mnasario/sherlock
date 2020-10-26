package com.sherlock.game.core.domain.message;

import lombok.Data;

@Data
public class Message {

    private Type type;
    private String value;
}