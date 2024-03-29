package com.sherlock.game.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherlock.game.core.domain.message.Envelop;
import com.sherlock.game.core.domain.message.Subject;
import com.sherlock.game.core.domain.message.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(Player.CLASS_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
public class Player {

    public static final String CLASS_NAME = "Player";

    private String name;
    private Boolean finishedGame;

    @JsonIgnore
    private List<Score> scores;

    @JsonIgnore
    @Transient
    private Session session;

    @Transient
    public Boolean getOnline() {
        return nonNull(session) && session.isOpen();
    }

    @JsonIgnore
    @Transient
    public Boolean isOffline() {
        return !getOnline();
    }

    @JsonIgnore
    @Transient
    public Envelop send(Envelop envelop) {

        if (getOnline()) {

            log.info("Session id: [{}] Player to: [{}] Message: [{}]", session.getId(), getName(), envelop.getPayload());
            session.getAsyncRemote().sendObject(envelop, result -> {
                if (nonNull(result.getException()))
                    log.error("Unable to send message content to player " + getName(), result.getException());
            });
        }

        return envelop;
    }

    @JsonIgnore
    @Transient
    public <T> Envelop send(Type type, Subject subject, T payload, ObjectMapper mapper) {

        Envelop message = Envelop.builder().mapper(mapper).type(type).subject(subject).build().putPayload(payload);
        return send(message);
    }

    @JsonIgnore
    @Transient
    public void addScore(Score score) {
        if (isNull(scores)) scores = new ArrayList<>();
        scores.add(score);
    }

    @JsonIgnore
    @Transient
    public int getScoresAmount() {
        return nonNull(scores) ? scores.size() : 0;
    }

    @JsonIgnore
    @Transient
    public boolean hasFinishedGame() {
        return nonNull(finishedGame) && finishedGame;
    }

    @JsonIgnore
    @Transient
    public boolean hasNotFinishedGame() {
        return !hasFinishedGame();
    }

    @JsonIgnore
    @Transient
    public boolean isEqualName(String name) {
        return nonNull(this.name) && this.name.equalsIgnoreCase(name);
    }
}
