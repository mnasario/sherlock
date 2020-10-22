package com.sherlock.game.core.controller;

import com.sherlock.game.core.domain.Marker;
import com.sherlock.game.core.service.MarkerService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/marker")
@AllArgsConstructor
public class MarkerRestController {

    private final MarkerService markerService;

    @CrossOrigin
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Marker> insert(@RequestBody Marker marker) {
        return ResponseEntity.ok(markerService.insert(marker));
    }

    @CrossOrigin
    @DeleteMapping(path = "/{markerId}")
    public ResponseEntity<Marker> delete(@PathVariable("markerId") UUID markerId) {
        markerService.delete(markerId);
        return ResponseEntity.ok().build();
    }
}
