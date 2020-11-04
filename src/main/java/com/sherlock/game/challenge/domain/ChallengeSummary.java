package com.sherlock.game.challenge.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sherlock.game.core.domain.ScoreSummary;
import lombok.Data;

import java.util.SortedSet;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChallengeSummary {

    private String gameId;
    private ChallengeConfig gameConfig;
    private SortedSet<ScoreSummary> rankedList;
}
