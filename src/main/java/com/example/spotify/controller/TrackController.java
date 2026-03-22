package com.example.spotify.controller;

import com.example.spotify.dto.TrackDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.spotify.entity.Track;
import com.example.spotify.service.TrackService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracks")
@CrossOrigin(origins = "*")
public class TrackController {
    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Track> getTrackById(@PathVariable Long id) {
        Track track = trackService.getTrackByID(id);
        return ResponseEntity.ok(track);
    }

    @GetMapping("/next")
    public ResponseEntity<Track> getNextTrack(
            @RequestParam(required = false) Long currentId) {

        Track track = trackService.getNextTrack(currentId);

        if (track == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(track);
    }

    @GetMapping("/recommended-tracks")
    public ResponseEntity<List<TrackDto>> getRandomTracks() {
        return ResponseEntity.ok(trackService.getRandomTracks());
    }

    @GetMapping("/by-album-id/{id}")
    public ResponseEntity<List<TrackDto>> getTrackByAlbumId(@PathVariable Long id) {
        return ResponseEntity.ok(trackService.getTracksByAlbumId(id));
    }

    @GetMapping("/by-artist-id/{id}")
    public ResponseEntity<List<TrackDto>> getTrackByArtistId(@PathVariable Long id) {
        return ResponseEntity.ok(trackService.getTracksByArtistId(id));
    }
      
    @GetMapping("/{trackId}/stream")
    public ResponseEntity<Map<String, Object>> getStreamByTrackId(@PathVariable Long trackId) {
        String audioUrl = trackService.getStreamUrlByTrackId(trackId);
        return ResponseEntity.ok(Map.of(
                "track_id", trackId,
                "audio_url", audioUrl
        ));
    }

    @GetMapping("/previous")
    public ResponseEntity<Track> getPreviousTrack(
            @RequestParam(required = false) Long currentId) {
        Track track = trackService.getPreviousTrack(currentId);

        if (track == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(track);
    }

    @GetMapping("/recently-played/{userId}")
    public ResponseEntity<List<TrackDto>> getRecentlyPlayed(@PathVariable Long userId) {
        return ResponseEntity.ok(trackService.getRecentlyPlayedTracks(userId));
    }

}
