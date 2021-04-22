package com.sherlock.game.challenge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherlock.game.challenge.exception.ChallengeRoomNotAvailableException;
import com.sherlock.game.core.domain.Credentials;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.ScoreSummary;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Subject;
import com.sherlock.game.core.domain.message.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;

import static com.sherlock.game.core.domain.message.Subject.GAME_FINISHED;
import static com.sherlock.game.core.domain.message.Type.INFO;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(ChallengeRoom.CLASS_NAME)
@Document(collection = ChallengeRoom.COLLECTION_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChallengeRoom {

    public static final String CLASS_NAME = "ChallengeRoom";
    public static final String COLLECTION_NAME = "challenge-rooms";

    @Id
    private String gameId;
    private ChallengeConfig gameConfig;
    private Boolean started;
    private Boolean finished;

    @JsonIgnore
    @Transient
    private ObjectMapper mapper;

    @JsonIgnore
    private Map<String, Player> playersMap;

    @JsonIgnore
    @Transient
    private ChallengeSummary summary;

    public Collection<Player> getPlayers() {
        return Optional.ofNullable(playersMap)
                .map(Map::values)
                .orElse(emptyList());
    }

    @Transient
    @JsonIgnore
    public Envelop broadcast(Type type, Subject subject) {
        return broadcast(type, subject, null);
    }

    @Transient
    @JsonIgnore
    public <T> Envelop broadcast(Type type, Subject subject, T payload) {

        Envelop message = Envelop.builder()
                .mapper(mapper)
                .type(type)
                .subject(subject)
                .build()
                .putPayload(payload);

        if (nonNull(getPlayers())) getPlayers().forEach(player -> player.send(message));
        return message;
    }

    @Transient
    @JsonIgnore
    public ChallengeRoom addPlayerToRoom(Credentials credentials) {
        String playerName = credentials.getPlayerName();
        Session session = credentials.getSession();
        getPlayersMap().putIfAbsent(playerName, Player.builder().name(playerName).session(session).build());
        return this;
    }

    @Transient
    @JsonIgnore
    public boolean isAvailable() {
        if (hasNotStarted()) return true;
        throw new ChallengeRoomNotAvailableException();
    }

    @Transient
    @JsonIgnore
    public boolean hasStarted() {
        return nonNull(started) && started;
    }

    @Transient
    @JsonIgnore
    public boolean hasNotStarted() {
        return !hasStarted();
    }


    @Transient
    @JsonIgnore
    public boolean hasFinished() {
        return nonNull(finished) && finished;
    }

    @Transient
    @JsonIgnore
    public boolean hasNotFinished() {
        return !hasFinished();
    }

    @Transient
    @JsonIgnore
    public ChallengeSummary getSummary() {

        return Optional.ofNullable(summary).orElse(
                ChallengeSummary.builder()
                        .gameId(gameId)
                        .gameConfig(gameConfig)
                        .rankedList(new TreeSet<>(Comparator.comparing(ScoreSummary::getTotalScore)))
                        .build());
    }

    public void triggerTimer() {
        log.debug("Game Id {} has started", gameId);

        TimerTask task = new TimerTask() {
            public void run() {
                triggerTimeout();
            }
        };
        Timer timer = new Timer("Timer-" + gameId);
        long delay = getGameConfig().getTimerInSeconds() * 1000L;
        timer.schedule(task, delay);
    }

    public void triggerTimeout() {
        log.debug("Game Id {} has finished", gameId);

        broadcast(INFO, GAME_FINISHED);
        setFinished(true);
        getPlayers().forEach(player -> {
            try {
                log.trace("Closing session to player {}", player.getName());
                player.getSession().close();
            } catch (IOException e) {
                log.error("Error to finish session to user: " + player.getName(), e);
            }
        });
    }
}
