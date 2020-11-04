package com.sherlock.game.core.domain;

import lombok.Data;

@Data
public class Score {

    private Marker marker;
    private Marker pinnedMarker;
    private Double distance;
    private Double scoreValue;
}
