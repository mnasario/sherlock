package com.sherlock.game.challenge.controller;

import com.sherlock.game.challenge.domain.ChallengeConfig;
import com.sherlock.game.challenge.domain.ChallengeRoom;
import com.sherlock.game.challenge.domain.ChallengeSummary;
import com.sherlock.game.challenge.service.ChallengeService;
import com.sherlock.game.core.domain.Player;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@RequestMapping("/challenge")
public class ChallengeRestController {

    private ChallengeService challengeService;

    @CrossOrigin
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChallengeRoom> insert(@RequestBody ChallengeConfig config) {

        try {

            return ResponseEntity.ok(challengeService.insert(config));

        } catch (IllegalArgumentException ex) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @CrossOrigin
    @GetMapping(path = "/{gameId}/room", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChallengeRoom> getRoom(@PathVariable("gameId") String gameId) {

        try {

            return ResponseEntity.ok(challengeService.getRoom(gameId));

        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @CrossOrigin
    @GetMapping(path = "/{gameId}/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChallengeSummary> getSummary(@PathVariable("gameId") String gameId) {

        try {

            return ResponseEntity.ok(challengeService.getSummary(gameId));

        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @CrossOrigin
    @GetMapping(path = "/{gameId}/player", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> getPlayer(@PathVariable("gameId") String gameId, @RequestParam("player") String playerName) {

        try {

            return ResponseEntity.ok(challengeService.getPlayer(gameId, playerName));

        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }
}
