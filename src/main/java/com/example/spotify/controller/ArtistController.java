package com.example.spotify.controller;

import com.example.spotify.dto.ArtistDto;
import com.example.spotify.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
@CrossOrigin
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping("/popular-artists")
    public ResponseEntity<List<ArtistDto>> get10PopularArtists() {
        return ResponseEntity.ok(artistService.get10PopularArtists());
    }

    @GetMapping("/all-artists")
    public ResponseEntity<List<ArtistDto>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }
}
