package com.sherlock.game.challenge.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sherlock.game.core.domain.Marker;
import com.sherlock.game.core.domain.Player;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChallengeConfig {

    private Player playerHost;
    private Long timerInSeconds;
    private Integer markersAmount;
    private Integer playersAmount;
    private List<Marker> markers;
}
