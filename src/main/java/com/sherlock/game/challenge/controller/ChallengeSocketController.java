package com.sherlock.game.challenge.controller;

import com.sherlock.game.challenge.service.ChallengeService;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.support.MessageDecoder;
import com.sherlock.game.support.MessageEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Slf4j
@Component
@CrossOrigin
@AllArgsConstructor
@ServerEndpoint(value = "/game/challenge/{gameId}/player/{player}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChallengeSocketController {

    private final ChallengeService challengeService;

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
    public void processMessage(Envelop message,
                               @PathParam("gameId") String gameId,
                               @PathParam("player") String player) {

        challengeService.processMessage(gameId, message, player);
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
