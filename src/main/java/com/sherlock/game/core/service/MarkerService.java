package com.sherlock.game.core.service;

import com.sherlock.game.core.domain.Marker;

import java.util.UUID;

public interface MarkerService {

    Marker insert(Marker marker);

    void delete(UUID markerId);

    Marker getRandomMapPoint();
}
