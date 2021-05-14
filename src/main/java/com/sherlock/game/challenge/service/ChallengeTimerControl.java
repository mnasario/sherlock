package com.sherlock.game.challenge.service;

import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.MessageRequest;
import com.sherlock.game.challenge.processor.GameSummarizedMessageProcessor;
import com.sherlock.game.core.domain.Player;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;

import static com.sherlock.game.core.domain.message.Subject.GAME_SUMMARIZED;
import static com.sherlock.game.core.domain.message.Type.INFO;

@Slf4j
@AllArgsConstructor
@Component
public class ChallengeTimerControl {
    private final GameSummarizedMessageProcessor messageProcessor;

    public void triggerTimer(ChallengeRoom room) {

        TimerTask task = new TimerTask() {
            public void run() {
                processGameOver(room);
            }
        };
        Timer timer = new Timer("Timer-" + room.getGameId());
        long delay = room.getGameConfig().getTimerInSeconds() * 1000L;
        timer.schedule(task, delay);
    }

    public void processGameOver(ChallengeRoom room) {
        if (room.hasNotSummarizedGame()) {
            room.gameOver();
            room.getPlayers().forEach(player -> processGameOver(room, player));
        }
        if (room.hasSummarizedGame()) room.broadcast(INFO, GAME_SUMMARIZED, room.getSummary());
    }

    public void processGameOver(ChallengeRoom room, Player player) {
        log.trace("Summarizing game to player {}", player.getName());

        int totalMarkers = room.getGameConfig().getMarkersAmount();
        int totalMarkersPinned = player.getScoresAmount();
        player.setFinishedGame(totalMarkers == totalMarkersPinned);
        messageProcessor.process(MessageRequest.builder().room(room).player(player).build());
    }
}
