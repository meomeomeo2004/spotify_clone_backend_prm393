package com.example.spotify.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.spotify.entity.Track;
import com.example.spotify.service.TrackService;

@RestController
@CrossOrigin(origins = "*")
public class TrackController {
    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping("/tracks/{id}")
    public ResponseEntity<Track> getTrackById(@PathVariable Long id) {
        Track track = trackService.getTrackByID(id);
        return ResponseEntity.ok(track);
    }

}
