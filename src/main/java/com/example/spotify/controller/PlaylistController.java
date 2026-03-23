package com.example.spotify.controller;

import com.example.spotify.dto.AddTrackToPlaylistRequest;
import com.example.spotify.dto.CreatePlaylistRequest;
import com.example.spotify.dto.PlaylistDetailDto;
import com.example.spotify.dto.PlaylistDto;
import com.example.spotify.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPlaylistsByUser(@PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid userId"));
        }
        List<PlaylistDto> playlists = playlistService.getPlaylistsByUserId(userId);
        return ResponseEntity.ok(playlists);
    }

    @PostMapping
    public ResponseEntity<?> createPlaylist(@RequestBody CreatePlaylistRequest body) {
        try {
            PlaylistDto created = playlistService.createPlaylist(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", e.getReason() != null ? e.getReason() : "Error"));
        }
    }

    @GetMapping("/{playlistId}/user/{userId}")
    public ResponseEntity<?> getPlaylistDetail(
            @PathVariable Long playlistId,
            @PathVariable Long userId) {
        try {
            PlaylistDetailDto detail = playlistService.getPlaylistDetail(playlistId, userId);
            return ResponseEntity.ok(detail);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", e.getReason() != null ? e.getReason() : "Error"));
        }
    }

    @PostMapping("/{playlistId}/tracks")
    public ResponseEntity<?> addTrack(
            @PathVariable Long playlistId,
            @RequestBody AddTrackToPlaylistRequest body) {
        try {
            PlaylistDetailDto detail = playlistService.addTrackToPlaylist(playlistId, body);
            return ResponseEntity.ok(detail);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", e.getReason() != null ? e.getReason() : "Error"));
        }
    }

    @DeleteMapping("/{playlistId}/tracks/{trackId}")
    public ResponseEntity<?> removeTrack(
            @PathVariable Long playlistId,
            @PathVariable Long trackId,
            @RequestParam Long userId) {
        try {
            PlaylistDetailDto detail = playlistService.removeTrackFromPlaylist(playlistId, trackId, userId);
            return ResponseEntity.ok(detail);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", e.getReason() != null ? e.getReason() : "Error"));
        }
    }
}
