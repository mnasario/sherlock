package com.sherlock.game.config;

import com.sherlock.game.challenge.controller.ChallengeSocketController;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@AllArgsConstructor
public class SherlockConfig implements WebSocketConfigurer {

    private final ChallengeSocketController challengeSocketController;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(challengeSocketController, "/socket").setAllowedOrigins("*");
    }

}
