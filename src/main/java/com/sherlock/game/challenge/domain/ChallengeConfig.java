package com.sherlock.game.challenge.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sherlock.game.core.domain.Marker;
import com.sherlock.game.core.domain.Player;
import lombok.Data;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@TypeAlias(ChallengeConfig.CLASS_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChallengeConfig {

    public static final String CLASS_NAME = "ChallengeConfig";

    @NonNull
    private Player playerHost;

    @NonNull
    private Long timerInSeconds;

    @NonNull
    private Integer markersAmount;

    @Nullable
    private Integer playersAmount;

    @Nullable
    private List<Marker> markers;
}
