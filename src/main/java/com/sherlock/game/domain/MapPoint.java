package com.sherlock.game.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MapPoint {

    private String description;
    private Double latitude;
    private Double longitude;
}
