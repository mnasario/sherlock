package com.sherlock.game.core.controller;

import com.sherlock.game.core.domain.Marker;
import com.sherlock.game.core.service.MarkerService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/markers")
@AllArgsConstructor
public class MarkerRestController {

    private final MarkerService markerService;

    @CrossOrigin
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Marker> insert(@RequestBody Marker marker) {
        return ResponseEntity.ok(markerService.insert(marker));
    }

    @CrossOrigin
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Marker>> findAll() {
        return ResponseEntity.ok(markerService.findAll());
    }

    @CrossOrigin
    @DeleteMapping(path = "/{markerId}")
    public ResponseEntity<Marker> delete(@PathVariable("markerId") UUID markerId) {
        markerService.delete(markerId);
        return ResponseEntity.ok().build();
    }
}
