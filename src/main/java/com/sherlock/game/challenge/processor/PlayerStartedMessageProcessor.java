package com.sherlock.game.challenge.processor;

import com.sherlock.game.challenge.domain.ChallengeConfig;
import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.MessageRequest;
import com.sherlock.game.core.domain.Marker;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Subject;
import com.sherlock.game.core.service.MarkerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.sherlock.game.core.domain.message.Subject.GAME_STARTED;
import static com.sherlock.game.core.domain.message.Subject.PLAYER_STARTED;
import static com.sherlock.game.core.domain.message.Type.INFO;

@Component
@AllArgsConstructor
public class PlayerStartedMessageProcessor implements ChallengeMessageProcessor {

    private final MarkerService markerService;

    @Override
    public Subject getSubject() {
        return PLAYER_STARTED;
    }

    @Override
    public Envelop process(MessageRequest messageRequest) {

        ChallengeRoom room = messageRequest.getRoom();
        ChallengeConfig gameConfig = room.getGameConfig();
        gameConfig.setMarkers(fetchRandomMarkers(gameConfig.getMarkersAmount()));
        room.triggerTimer();
        room.setStarted(true);
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
