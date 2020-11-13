package com.sherlock.game.core.domain;

import lombok.Builder;
import lombok.Data;

import javax.websocket.Session;

@Data
@Builder
public class Credentials {

    private String gameId;
    private String playerName;
    private Session session;
}
