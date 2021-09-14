package com.sherlock.game.core.service;

import com.sherlock.game.core.domain.Marker;
import com.sherlock.game.core.exception.MarkerConflictException;
import com.sherlock.game.core.exception.MarkerNotFoundException;
import com.sherlock.game.core.repository.MarkerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.UUID;

import static com.sherlock.game.support.GameHandler.RANDOM;

@Service
@Slf4j
@AllArgsConstructor
public class MarkerServiceImpl implements MarkerService {

    private final MarkerRepository markerRepository;

    @Override
    public Marker insert(Marker marker) {

        Assert.notNull(marker, "Marker is required");
        Assert.notNull(marker.getDescription(), "Marker description is required");
        Assert.notNull(marker.getLatitude(), "Latitude is required");
        Assert.notNull(marker.getLongitude(), "Longitude is required");
        Assert.isTrue(validateMarkerExists(marker), "Marker is already exists");
        marker.setId(UUID.randomUUID());
        return markerRepository.save(marker);
    }

    @Override
    public void delete(UUID markerId) {

        Assert.isTrue(markerRepository.existsById(markerId), "Valid marker id is required");
        markerRepository.deleteById(markerId);
    }

    @Override
    public Marker getRandomMapPoint() {

        long total = markerRepository.count();
        Assert.isTrue(total > 0, "Database is empty, please, fill it");
        int selected = RANDOM.nextInt(Long.valueOf(total).intValue());
        Page<Marker> markers = markerRepository.findAll(PageRequest.of(selected, 1));
        return markers.get().findAny().orElseThrow(MarkerNotFoundException::new);
    }

    @Override
    public Collection<Marker> findAll() {
        return markerRepository.findAll();
    }

    private boolean validateMarkerExists(Marker marker) {

        if(markerRepository.exists(Example.of(Marker.builder()
                .latitude(marker.getLatitude())
                .longitude(marker.getLongitude())
                .build()))){
            throw new MarkerConflictException();
        }

        return true;
    }
}
