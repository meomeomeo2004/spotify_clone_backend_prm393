package com.example.spotify.service;

import com.example.spotify.dto.ArtistDto;
import com.example.spotify.repository.ArtistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public List<ArtistDto> get10PopularArtists() {
        return artistRepository.findTop10Artist()
                .stream()
                .map(artist -> ArtistDto.builder()
                        .id(artist.getId())
                        .imageUrl(artist.getImageUrl())
                        .name(artist.getName())
                        .bio(artist.getBio())
                        .build()).toList();
    }

    public List<ArtistDto> getAllArtists() {
        return artistRepository.findAllArtist()
                .stream()
                .map(artist -> ArtistDto.builder()
                        .id(artist.getId())
                        .imageUrl(artist.getImageUrl())
                        .name(artist.getName())
                        .build()).toList();
    }
}
