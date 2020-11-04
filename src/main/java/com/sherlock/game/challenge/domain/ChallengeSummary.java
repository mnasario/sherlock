package com.sherlock.game.challenge.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sherlock.game.core.domain.ScoreSummary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import java.util.SortedSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(ChallengeRoom.CLASS_NAME)
@Document(collection = ChallengeRoom.COLLECTION_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChallengeSummary {

    public static final String CLASS_NAME = "ChallengeSummary";
    public static final String COLLECTION_NAME = "challenge-summary";

    @Id
    private String gameId;

    @NonNull
    private ChallengeConfig gameConfig;

    @NonNull
    private SortedSet<ScoreSummary> rankedList;
}
