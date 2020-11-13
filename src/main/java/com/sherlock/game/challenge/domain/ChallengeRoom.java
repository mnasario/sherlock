package com.sherlock.game.challenge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherlock.game.challenge.exception.ChallengeRoomNotAvailableException;
import com.sherlock.game.core.domain.Credentials;
import com.sherlock.game.core.domain.Player;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Subject;
import com.sherlock.game.core.domain.message.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.websocket.Session;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

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
    private Boolean isStarted;
    private Boolean isFinished;

    @JsonIgnore
    @Transient
    private ObjectMapper mapper;

    @JsonIgnore
    private Map<String, Player> playersMap;

    public Collection<Player> getPlayers() {
        return Optional.ofNullable(playersMap)
                .map(Map::values)
                .orElse(emptyList());
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
        Session session = credentials.getSession();
        String playerName = credentials.getPlayerName();
        getPlayersMap().putIfAbsent(playerName, Player.builder().name(playerName).session(session).build());
        return this;
    }

    @Transient
    @JsonIgnore
    public boolean isAvailable() {
        if (nonNull(isStarted) && isStarted) throw new ChallengeRoomNotAvailableException();
        return true;
    }

    @Transient
    @JsonIgnore
    public boolean isEnded() {
        return nonNull(isFinished) && getIsFinished();
    }

    @Transient
    @JsonIgnore
    public boolean isNotEnded() {
        return !isEnded();
    }

    public void triggerGameTimeout() {
        //TODO Criar mecanismo de timeout para o jogo
    }
}
