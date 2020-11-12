package com.sherlock.game.challenge.controller;

import com.sherlock.game.CustomSpringConfigurator;
import com.sherlock.game.challenge.service.ChallengeService;
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
    public void open(@PathParam("gameId") String gameId,
                     @PathParam("player") String player,
                     Session session) {

        challengeService.login(session, gameId, player);
    }

    @OnMessage
    public void processMessage(@PathParam("gameId") String gameId,
                               @PathParam("player") String player,
                               Session session,
                               Envelop message) {

        challengeService.processMessage(session, gameId, player, message);
    }

    @OnClose
    public void close(@PathParam("gameId") String gameId,
                      @PathParam("player") String player,
                      Session session) {

        challengeService.summarize(session, gameId, player);
    }

    @OnError
    public void processError(Session session,
                             Throwable throwable) {

        challengeService.processError(session, throwable);
    }
}
