package com.sherlock.game.challenge.controller;

import com.sherlock.game.challenge.service.ChallengeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Component
public class ChallengeSocketController extends AbstractWebSocketHandler {

    private final ChallengeService challengeService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.info("New Text Message Received");
        session.sendMessage(message);
    }
}
