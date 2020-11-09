package com.sherlock.game.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.TypeAlias;

@Data
@TypeAlias(Score.CLASS_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Score {

    public static final String CLASS_NAME = "Score";

    private Marker marker;
    private Marker pinnedMarker;
    private Double distance;
    private Double scoreValue;
}
