package com.example.spotify.controller;

import com.example.spotify.dto.AlbumDto;
import com.example.spotify.entity.Album;
import com.example.spotify.service.AlbumService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@CrossOrigin
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<Album>> getAlbumsByArtist(@PathVariable Long artistId) {
        List<Album> albums = albumService.getAlbumsByArtistId(artistId);

        if (albums.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/popular-albums")
    public ResponseEntity<List<AlbumDto>> getPopularAlbums() {
        return ResponseEntity.ok(albumService.getPopularAlbums());
    }
}
