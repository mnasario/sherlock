package com.sherlock.game.challenge.service;

import com.sherlock.game.domain.MapPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class ChallengeServiceImpl implements ChallengeService {

    private static final Random RANDOM = new Random();

    private List<MapPoint> mapPoints = new ArrayList<>();

    @Override
    public MapPoint getRandomMapPoint() {
        Assert.notEmpty(mapPoints, "Map points is empty, please, fill it");
        return mapPoints.get(RANDOM.nextInt(mapPoints.size()));
    }


    @EventListener(ApplicationReadyEvent.class)
    public void loadMapPoints() {
        mapPoints.add(MapPoint.builder().description("Postdamer Platz").latitude(52.511884).longitude(13.376432).build());
        mapPoints.add(MapPoint.builder().description("Parkcafé Berlin").latitude(52.491272).longitude(13.314484).build());
        mapPoints.add(MapPoint.builder().description("Café am Neue See").latitude(52.510495).longitude(13.344472).build());
    }
}
