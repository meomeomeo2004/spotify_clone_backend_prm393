package com.example.spotify.controller;

import com.example.spotify.dto.PlaylistDto;
import com.example.spotify.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * GET /api/playlists/user/{userId}  — all playlists for a user
 */
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
}
