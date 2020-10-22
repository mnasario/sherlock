package com.sherlock.game.training.controller;


import com.sherlock.game.core.domain.Marker;
import com.sherlock.game.core.service.MarkerService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//TODO Waiting FE update
//@RequestMapping("/training")
@AllArgsConstructor
public class TrainingRestController {

    private final MarkerService markerService;

    @CrossOrigin
    @GetMapping(path = "/training/random-marker", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Marker> getRandomMarker() {
        return ResponseEntity.ok(markerService.getRandomMapPoint());
    }

    @CrossOrigin
    @GetMapping(path = "/challenge/random-map-point", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Marker> getRandomMapPoint() {
        return getRandomMarker();
    }
}
