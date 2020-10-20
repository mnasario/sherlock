package com.sherlock.game.challenge.controller;


import com.sherlock.game.challenge.service.ChallengeService;
import com.sherlock.game.domain.MapPoint;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/challenge")
@AllArgsConstructor
public class ChallengeRestController {

    private final ChallengeService challengeService;

    @CrossOrigin
    @GetMapping("/random-map-point")
    public ResponseEntity<MapPoint> getRandomMapPoint() {
        return ResponseEntity.ok(challengeService.getRandomMapPoint());
    }
}
