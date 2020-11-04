package com.sherlock.game.challenge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sherlock.game.core.domain.Player;
import lombok.Data;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChallengeRoom {

    private String gameId;
    private ChallengeConfig gameConfig;

    @JsonIgnore
    private Map<String, Player> players;

    public Collection<Player> getPlayers() {
        return Optional.ofNullable(players)
                .map(Map::values)
                .orElse(emptyList());
    }
}
