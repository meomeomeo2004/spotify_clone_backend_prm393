package com.example.spotify.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.spotify.entity.Track;
import com.example.spotify.service.TrackService;

import java.util.Map;

@RestController
@RequestMapping("/api")
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

    @GetMapping("/tracks/next")
    public ResponseEntity<Track> getNextTrack(
            @RequestParam(required = false) Long currentId) {

        Track track = trackService.getNextTrack(currentId);

        if (track == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(track);
    }
    @GetMapping("/tracks/previous")
    public ResponseEntity<Track> getPreviousTrack(
            @RequestParam(required = false) Long currentId) {

        Track track = trackService.getPreviousTrack(currentId);

        if (track == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(track);
    }
    @GetMapping("/tracks/{trackId}/stream")
    public ResponseEntity<Map<String, Object>> getStreamByTrackId(@PathVariable Long trackId) {
        String audioUrl = trackService.getStreamUrlByTrackId(trackId);
        return ResponseEntity.ok(Map.of(
                "track_id", trackId,
                "audio_url", audioUrl
        ));
    }
}
