package com.sherlock.game.challenge.controller;

import com.sherlock.game.CustomSpringConfigurator;
import com.sherlock.game.challenge.service.ChallengeService;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.support.MessageDecoder;
import com.sherlock.game.support.MessageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Slf4j
@Component
@ServerEndpoint(value = "/game/challenge/{gameId}/player/{player}",
        decoders = MessageDecoder.class, encoders = MessageEncoder.class,
        configurator = CustomSpringConfigurator.class)
public class ChallengeSocketEndpoint {

    private final ChallengeService challengeService;

    @Autowired
    public ChallengeSocketEndpoint(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @OnOpen
    public void open(Session session,
                     @PathParam("gameId") String gameId,
                     @PathParam("player") String player) {

        challengeService.login(gameId,
                Player.builder()
                        .name(player)
                        .session(session)
                        .build());
    }

    @OnMessage
    public void processMessage(@PathParam("gameId") String gameId,
                               @PathParam("player") String player,
                               Envelop message) {

        challengeService.processMessage(gameId, player, message);
    }

    @OnClose
    public void close(@PathParam("gameId") String gameId, @PathParam("player") String player) {

        challengeService.summarize(gameId, player);
    }

    @OnError
    public void processError(@PathParam("gameId") String gameId,
                             @PathParam("player") String player,
                             Throwable throwable) {

        challengeService.processError(gameId, player, throwable);
    }
}
