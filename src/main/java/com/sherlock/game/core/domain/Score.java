package com.sherlock.game.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Data
@TypeAlias(Score.CLASS_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Score {

    public static final String CLASS_NAME = "Score";

    @NonNull
    private Marker marker;

    @NonNull
    private Marker pinnedMarker;

    @NonNull
    private Double distance;

    @Nullable
    private Double scoreValue;
}
