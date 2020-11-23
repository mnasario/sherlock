package com.sherlock.game.challenge.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.MessageRequest;
import com.sherlock.game.core.domain.Marker;
import com.sherlock.game.core.domain.MarkerPin;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.Score;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Subject;
import com.sherlock.game.core.exception.MarkerNotFoundException;
import com.sherlock.game.core.exception.PayloadConvertException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static com.sherlock.game.core.domain.message.Subject.GAME_FINISHED;
import static com.sherlock.game.core.domain.message.Subject.PLAYER_PINNED;
import static com.sherlock.game.core.domain.message.Type.INFO;
import static com.sherlock.game.core.domain.message.Type.SYSTEM;
import static java.math.RoundingMode.HALF_EVEN;

@Component
@AllArgsConstructor
public class PlayerPinnedMessageProcessor implements ChallengeMessageProcessor {

    private static final int DISTANCE_50M = 50;
    private static final int DISTANCE_100M = 100;
    private static final int DISTANCE_150M = 150;
    private static final int DISTANCE_200M = 200;
    private static final int DISTANCE_1000KM = 1_000;
    private static final int DISTANCE_10_000KM = 10_000;
    private static final int DISTANCE_14_500KM = 14_500;
    private static final int SCORE_FIBONACCI_LIMIT = 20;
    private static final int SCORE_360M_LIMIT = 70;

    private final ObjectMapper mapper;

    @Override
    public Subject getSubject() {
        return PLAYER_PINNED;
    }

    @Override
    public Envelop process(MessageRequest messageRequest) {

        ChallengeRoom room = messageRequest.getRoom();
        Player player = messageRequest.getPlayer();
        MarkerPin markerPinned = convertToMarkerPin(messageRequest);
        validateMarkerPinned(markerPinned);
        Marker marker = getMarkerBy(markerPinned.getMarker().getId(), room);
        markerPinned.setMarker(marker);
        markerPinned.getPinnedMarker().setId(marker.getId());

        Score score = Score.builder()
                .marker(marker)
                .pinnedMarker(markerPinned.getPinnedMarker())
                .distance(markerPinned.getDistance())
                .scoreValue(getScoreByDistance(markerPinned.getDistance()))
                .build();

        player.addScore(score);
        player.setFinishedGame(room.getGameConfig().getMarkersAmount() == player.getScores().size());
        if (player.hasFinishedGame()) {
            room.broadcast(SYSTEM, GAME_FINISHED, player);
            return player.send(SYSTEM, GAME_FINISHED, score, mapper);
        }

        room.broadcast(INFO, PLAYER_PINNED, player);
        return player.send(SYSTEM, PLAYER_PINNED, score, mapper);
    }

    private Marker getMarkerBy(UUID id, ChallengeRoom room) {
        return Optional.ofNullable(room.getGameConfig().getMarkerById(id))
                .orElseThrow(MarkerNotFoundException::new);
    }

    private void validateMarkerPinned(MarkerPin markerPinned) {

        Assert.notNull(markerPinned, "Marker pinned is required");
        Assert.notNull(markerPinned.getMarker(), "Marker is required");
        Assert.notNull(markerPinned.getMarker().getId(), "Marker id is required");
        Assert.notNull(markerPinned.getPinnedMarker(), "Marker pinned is required");
        Assert.notNull(markerPinned.getPinnedMarker().getLatitude(), "Marker pinned (lat) is required");
        Assert.notNull(markerPinned.getPinnedMarker().getLongitude(), "Marker pinned (lng) is required");
        Assert.notNull(markerPinned.getDistance(), "Distance is required");
    }

    private MarkerPin convertToMarkerPin(MessageRequest messageRequest) {
        try {
            String payload = StringUtils.replace(messageRequest.getEnvelop().getPayload(), "'", "\"");
            return mapper.readValue(payload, MarkerPin.class);
        } catch (JsonProcessingException e) {
            throw new PayloadConvertException("Error to convert payload to MarkerPinned", e);
        }
    }

    private double getScoreByDistance(double distanceInMeters) {

        if (distanceInMeters < DISTANCE_50M) return 100D;
        if (distanceInMeters < DISTANCE_100M) return 99D;
        if (distanceInMeters < DISTANCE_150M) return 98D;
        if (distanceInMeters < DISTANCE_200M) return 97D;
        if (distanceInMeters < DISTANCE_1000KM) return 100 - getScoreBasedOnFibonacci(distanceInMeters);

        double distanceInKm = distanceInMeters / 1000;
        if (distanceInKm < DISTANCE_10_000KM) return roundScore(100 - getScoreBasedOn360M(distanceInKm));
        if (distanceInKm < DISTANCE_14_500KM) return roundScore(100 - getScoreBasedOn450M(distanceInKm));

        return 0D;
    }

    private double getScoreBasedOnFibonacci(double distanceInMeters) {
        int result = 0;
        int index = 0;
        double distance = distanceInMeters / 100;
        while (result < distance) {
            index++;
            result = calcFibonacciRecursive(index);
        }
        return index;
    }

    private double getScoreBasedOn360M(double distanceInKm) {
        double result = (distanceInKm - DISTANCE_1000KM) / 360 * 2;
        return SCORE_FIBONACCI_LIMIT + result;
    }

    private double getScoreBasedOn450M(double distanceInKm) {
        double result = (distanceInKm - DISTANCE_10_000KM) / 450 * 3;
        return SCORE_360M_LIMIT + result;
    }

    private int calcFibonacciRecursive(int n) {

        if (n == 0) return 0;
        if (n == 1) return 1;
        return calcFibonacciRecursive(n - 1) + calcFibonacciRecursive(n - 2);
    }

    private double roundScore(double score) {
        return BigDecimal.valueOf(score).setScale(0, HALF_EVEN).doubleValue();
    }
}
