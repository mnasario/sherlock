package com.sherlock.game.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkerPin {

    private Player player;
    private Marker marker;
    private Marker pinnedMarker;
    private Double distance;
}
