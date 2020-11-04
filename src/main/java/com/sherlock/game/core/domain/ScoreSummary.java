package com.sherlock.game.core.domain;

import lombok.Data;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Data
public class ScoreSummary {

    private Player player;
    private Long totalScore;

    public Collection<Score> getScores() {
        return Optional.ofNullable(player).map(Player::getScores).orElse(Collections.emptyList());
    }
}
