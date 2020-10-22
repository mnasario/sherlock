package com.sherlock.game.health.deployment;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationReadyController {

    @GetMapping(value = "/ready", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> ready() {
        return ResponseEntity.ok("{\"status\":\"READY\"}");
    }

}