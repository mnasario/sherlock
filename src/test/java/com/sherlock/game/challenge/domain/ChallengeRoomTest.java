package com.sherlock.game.challenge.domain;

import org.junit.Test;

import static com.sherlock.game.support.GameHandler.generateRandomCode;

public class ChallengeRoomTest {

    @Test
    public void shouldTriggerTimerTestSuccess() {

        ChallengeConfig config = ChallengeConfig.builder()
                .timerInSeconds(10L)
                .build();

        String gameId = generateRandomCode(20);

        ChallengeRoom room = ChallengeRoom.builder()
                .gameId(gameId)
                .gameConfig(config)
                .build();

        room.triggerTimer();
    }
}
