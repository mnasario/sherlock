package com.sherlock.game.core.domain;

import lombok.Data;

@Data
public class MarkerPin {

    private Player player;
    private Marker marker;
    private Marker pinned;
    private Double distance;
}
