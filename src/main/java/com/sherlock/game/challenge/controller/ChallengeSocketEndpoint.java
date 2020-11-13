package com.sherlock.game.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sherlock.game.CustomSpringConfigurator;
import com.sherlock.game.challenge.service.ChallengeService;
import com.sherlock.game.core.domain.Credentials;
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
import java.io.IOException;

import static com.sherlock.game.core.domain.message.Subject.GAME_FAILED;
import static com.sherlock.game.core.domain.message.Subject.PLAYER_LEFT;
import static com.sherlock.game.core.domain.message.Type.ERROR;
import static com.sherlock.game.core.domain.message.Type.INFO;

@Slf4j
@Component
@ServerEndpoint(value = "/game/challenge/{gameId}/player/{player}",
        decoders = MessageDecoder.class, encoders = MessageEncoder.class,
        configurator = CustomSpringConfigurator.class)
public class ChallengeSocketEndpoint {

    private final ChallengeService challengeService;
    private final ObjectMapper mapper;

    @Autowired
    public ChallengeSocketEndpoint(ChallengeService challengeService, ObjectMapper mapper) {
        this.challengeService = challengeService;
        this.mapper = mapper;
    }

    @OnOpen
    public void open(@PathParam("gameId") String gameId,
                     @PathParam("player") String player,
                     Session session) {

        try {

            Credentials credentials = Credentials.builder()
                    .gameId(gameId)
                    .playerName(player)
                    .session(session)
                    .build();
            challengeService.login(credentials);

        } catch (Exception e) {
            pushErrorMessage(gameId, player, session, e);
        }
    }

    @OnMessage
    public void processMessage(@PathParam("gameId") String gameId,
                               @PathParam("player") String player,
                               Session session,
                               Envelop message) {
        try {

            Credentials credentials = Credentials.builder()
                    .gameId(gameId)
                    .playerName(player)
                    .session(session)
                    .build();
            challengeService.processMessage(credentials, message);

        } catch (Exception e) {
            pushErrorMessage(gameId, player, session, e);
        }
    }

    @OnClose
    public void close(@PathParam("gameId") String gameId,
                      @PathParam("player") String player,
                      Session session) throws IOException {
        try {

            Credentials credentials = Credentials.builder()
                    .gameId(gameId)
                    .playerName(player)
                    .session(session)
                    .build();
            challengeService.summarize(credentials);
        } catch (Exception e) {
            pushErrorMessage(gameId, player, session, e);
            challengeService.getRoom(gameId).broadcast(INFO, PLAYER_LEFT, Player.builder().name(player).build());
        } finally {
            session.close();
        }
    }

    @OnError
    public void processError(@PathParam("gameId") String gameId,
                             @PathParam("player") String player,
                             Session session,
                             Throwable throwable) {
        pushErrorMessage(gameId, player, session, throwable);
        challengeService.getRoom(gameId).broadcast(INFO, PLAYER_LEFT, Player.builder().name(player).build());
    }

    private void pushErrorMessage(@PathParam("gameId") String gameId,
                                  @PathParam("player") String player,
                                  Session session,
                                  Throwable throwable) {

        String errorMessage = String.format("Challenge socket error to gameId [%s] and player [%s]. Cause: [%s]",
                gameId, player, throwable.getMessage());
        log.error(errorMessage, throwable);

        ObjectNode node = mapper.createObjectNode();
        node.put("message", errorMessage);
        Envelop message = Envelop.builder()
                .mapper(mapper)
                .type(ERROR)
                .subject(GAME_FAILED)
                .build()
                .putPayload(node.toString());
        session.getAsyncRemote().sendObject(message);
    }
}
