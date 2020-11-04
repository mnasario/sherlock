package com.sherlock.game.core.domain;

import lombok.Data;

@Data
public class SpotMarked {

    private Player player;
    private Marker marker;
    private Double distance;
}
