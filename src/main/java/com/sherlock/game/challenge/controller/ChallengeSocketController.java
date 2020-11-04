package com.sherlock.game.challenge.controller;

import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Type;
import com.sherlock.game.support.MessageDecoder;
import com.sherlock.game.support.MessageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@CrossOrigin
@ServerEndpoint(value = "/game/challenge/{gameId}/player/{player}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChallengeSocketController {

    private static Map<String, Session> gamesMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("gameId") String gameId,
                       @PathParam("player") String player) {

        String key = getKey(gameId, player);
        gamesMap.putIfAbsent(key, session);
        broadcast(gameId, player, player + " is online!");
    }

    @OnClose
    public void onClose(Session session,
                        @PathParam("gameId") String gameId,
                        @PathParam("player") String player) {

        String key = getKey(gameId, player);
        gamesMap.remove(key);
        broadcast(gameId, player, "Player " + player + " left");
    }

    @OnError
    public void onError(Session session,
                        @PathParam("gameId") String gameId,
                        @PathParam("player") String player,
                        Throwable throwable) {

        String key = getKey(gameId, player);
        gamesMap.remove(key);
        log.error("onError", throwable);
        broadcast(gameId, player, "Player " + player + " left on error: " + throwable);
    }

    @OnMessage
    public void onMessage(Envelop message,
                          Session session,
                          @PathParam("gameId") String gameId,
                          @PathParam("player") String player) {

        log.info("Type: " + message.getType() + " - Value: " + message.getPayload());
        broadcast(gameId, player, ">> " + player + ": " + message.getPayload());
    }

    private void broadcast(String gameId, String player, String message) {

        gamesMap.forEach((key, session) -> {
            if (key.startsWith(gameId)) sendMessageTo(session, player, message);
        });
    }

    private void sendMessageTo(Session session, String player, String content) {

        log.info("Session: " + session.getId() + " - Player from: " + player + " - Message: " + content);
        Envelop message = new Envelop();
        message.setType(Type.INFO);
        message.setPayload(content);
        session.getAsyncRemote().sendObject(message, result -> {
            if (result.getException() != null)
                log.error("Unable to send message content from player " + player, result.getException());
        });
    }

    private String getKey(@PathParam("gameId") String gameId, @PathParam("player") String player) {
        return gameId + "_" + player;
    }
}
