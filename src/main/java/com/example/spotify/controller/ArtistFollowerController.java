package com.example.spotify.controller;

import com.example.spotify.dto.ArtistFollowerDto;
import com.example.spotify.service.ArtistFollowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/choose")
@RequiredArgsConstructor
public class ArtistFollowerController {

    private final ArtistFollowerService artistFollowerService;
    @PostMapping("/follow-artists")
    public ResponseEntity<?> followArtists(@RequestBody ArtistFollowerDto request) {
        try {
            artistFollowerService.saveUserArtists(request.getUserId(), request.getArtistIds());
            return ResponseEntity.ok().body("Message: Artists followed successfully");
        } catch (Exception e) {
            e.getMessage();
            return ResponseEntity.badRequest().body("Error: Failed to follow artists");
        }
    }
}