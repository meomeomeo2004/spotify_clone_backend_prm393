package com.example.spotify.controller;

import com.example.spotify.dto.AlbumDto;
import com.example.spotify.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
@CrossOrigin
public class AlbumController {
    private final AlbumService albumService;

    @GetMapping("/popular-albums")
    public ResponseEntity<List<AlbumDto>> getPopularAlbums() {
        return ResponseEntity.ok(albumService.getPopularAlbums());
    }
}
