package com.sherlock.game.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(ScoreSummary.CLASS_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScoreSummary {

    public static final String CLASS_NAME = "ScoreSummary";

    private Player player;
    private Long totalScore;

    public Collection<Score> getScores() {
        return Optional.ofNullable(player).map(Player::getScores).orElse(Collections.emptyList());
    }
}
