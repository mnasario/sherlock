package com.sherlock.game.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sherlock.game.challenge.domain.ChallengeConfig;
import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.MessageRequest;
import com.sherlock.game.core.domain.Marker;
import com.sherlock.game.core.domain.MarkerPin;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.Score;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Subject;
import com.sherlock.game.core.domain.message.Type;
import lombok.NoArgsConstructor;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Arrays.asList;

@NoArgsConstructor
public final class GameModelFactory {

    public static Player buildPlayer(String name, Session session) {

        return Player.builder()
                .name(name)
                .session(session)
                .build();
    }

    public static Envelop buildMessage(Type type, Subject subject) {

        return buildMessage(type, subject, null);
    }

    public static Envelop buildMessage(Type type, Subject subject, String payload) {

        Envelop message = Envelop.builder()
                .type(type)
                .subject(subject)
                .build();
        message.setPayload(payload);
        return message;
    }

    public static ChallengeConfig buildChallengeConfig(Player playerHost,
                                                       Long timerInSeconds,
                                                       Integer markersAmount,
                                                       Marker... markers) {
        return ChallengeConfig.builder()
                .playerHost(playerHost)
                .timerInSeconds(timerInSeconds)
                .markersAmount(markersAmount)
                .markers(markers.length > 0 ? asList(markers) : null)
                .build();
    }

    public static ChallengeRoom buildChallengeRoom(String gameId,
                                                   ChallengeConfig gameConfig,
                                                   Map<String, Player> playersMap) {

        return ChallengeRoom.builder()
                .mapper(buildMapper())
                .gameId(gameId)
                .gameConfig(gameConfig)
                .playersMap(playersMap)
                .build();
    }

    public static ObjectMapper buildMapper() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }

    public static MessageRequest buildMessageRequest(ChallengeRoom room, Player player, Envelop message) {

        return MessageRequest.builder()
                .room(room)
                .player(player)
                .envelop(message)
                .build();
    }

    public static Map<String, Player> buildPlayersMap(Player... players) {

        Map<String, Player> playersMap = new HashMap<>();
        if (players.length == 0) return playersMap;

        for (Player player : players) playersMap.put(player.getName(), player);
        return playersMap;
    }

    public static Marker buildMarker(UUID id, Double lat, Double lng) {

        return Marker.builder()
                .id(id)
                .latitude(lat)
                .longitude(lng)
                .build();
    }

    public static Score buildScore(Marker marker, Marker pinnedMarker, Double distance) {
        return Score.builder()
                .marker(marker)
                .pinnedMarker(pinnedMarker)
                .distance(distance)
                .build();
    }

    public static MarkerPin buildMarkerPin(Player player, Marker marker, Marker pinnedMarker, Double distance) {

        return MarkerPin.builder()
                .player(player)
                .marker(marker)
                .pinned(pinnedMarker)
                .distance(distance)
                .build();
    }
}
