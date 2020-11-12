package com.sherlock.game.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sherlock.game.core.domain.message.Envelop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;

import javax.websocket.Session;
import java.util.List;

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

    @JsonIgnore
    private List<Score> scores;

    @JsonIgnore
    @Transient
    private Session session;

    @JsonIgnore
    public Envelop send(Envelop envelop) {

        Session session = getSession();
        log.info("Session: " + session.getId() + " - Player from: " + getName() + " - Message: " + envelop.getPayload());
        session.getAsyncRemote().sendObject(envelop, result -> {
            if (result.getException() != null)
                log.error("Unable to send message content to player " + getName(), result.getException());
        });
        return envelop;
    }
}
