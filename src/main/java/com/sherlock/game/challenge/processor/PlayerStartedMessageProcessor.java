package com.sherlock.game.challenge.processor;

import com.sherlock.game.challenge.domain.ChallengeConfig;
import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.MessageRequest;
import com.sherlock.game.challenge.service.ChallengeTimerControl;
import com.sherlock.game.core.domain.Marker;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Subject;
import com.sherlock.game.core.service.MarkerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.sherlock.game.core.domain.message.Subject.GAME_STARTED;
import static com.sherlock.game.core.domain.message.Subject.PLAYER_STARTED;
import static com.sherlock.game.core.domain.message.Type.INFO;

@Slf4j
@Component
@AllArgsConstructor
public class PlayerStartedMessageProcessor implements ChallengeMessageProcessor {

    private final MarkerService markerService;
    private final ChallengeTimerControl challengeTimerControl;

    @Override
    public Subject getSubject() {
        return PLAYER_STARTED;
    }

    @Override
    public Envelop process(MessageRequest messageRequest) {

        ChallengeRoom room = messageRequest.getRoom();
        ChallengeConfig gameConfig = room.getGameConfig();
        gameConfig.setMarkers(fetchRandomMarkers(gameConfig.getMarkersAmount()));
        room.setStarted(true);
        challengeTimerControl.triggerTimer(room);
        log.debug("Game Id {} has started", room.getGameId());
        return room.broadcast(INFO, GAME_STARTED, room);
    }

    private List<Marker> fetchRandomMarkers(Integer amount) {

        Map<UUID, Marker> markers = new HashMap<>();
        while (markers.size() < amount) {

            Marker marker = markerService.getRandomMapPoint();
            markers.put(marker.getId(), marker);
        }
        return new ArrayList<>(markers.values());
    }
}
