package com.sherlock.game.challenge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sherlock.game.core.domain.Marker;
import com.sherlock.game.core.domain.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TypeAlias(ChallengeConfig.CLASS_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChallengeConfig {

    public static final String CLASS_NAME = "ChallengeConfig";

    private Player playerHost;
    private Long timerInSeconds;
    private Integer markersAmount;
    private Integer playersAmount;
    private List<Marker> markers;

    @Transient
    @JsonIgnore
    public Marker getMarkerById(UUID id) {

        if (isNull(markers) || isNull(id)) return null;
        return markers.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
