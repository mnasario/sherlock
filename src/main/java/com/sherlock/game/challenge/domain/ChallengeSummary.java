package com.sherlock.game.challenge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sherlock.game.core.domain.ScoreSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Optional;
import java.util.SortedSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(ChallengeRoom.CLASS_NAME)
@Document(collection = ChallengeSummary.COLLECTION_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChallengeSummary {

    public static final String CLASS_NAME = "ChallengeSummary";
    public static final String COLLECTION_NAME = "challenge-summary";

    @Id
    private String gameId;
    private ChallengeConfig gameConfig;
    private SortedSet<ScoreSummary> rankedList;

    @JsonIgnore
    @Transient
    public void addScoreSummary(ScoreSummary scoreSummary) {
        Optional.ofNullable(scoreSummary).ifPresent(s -> rankedList.add(s));
    }
}
