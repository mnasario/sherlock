package com.sherlock.game.challenge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sherlock.game.core.domain.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;

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

    @JsonIgnore
    private Map<String, Player> playersMap;

    public Collection<Player> getPlayers() {
        return Optional.ofNullable(playersMap)
                .map(Map::values)
                .orElse(emptyList());
    }
}
