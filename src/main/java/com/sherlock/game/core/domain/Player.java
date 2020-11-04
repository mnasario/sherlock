package com.sherlock.game.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.websocket.Session;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(Player.CLASS_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Player {

    public static final String CLASS_NAME = "Player";

    @NonNull
    private String name;

    @JsonIgnore
    @Nullable
    private List<Score> scores;

    @JsonIgnore
    @Transient
    private Session session;
}
