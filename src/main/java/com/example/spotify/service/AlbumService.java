package com.example.spotify.service;

import com.example.spotify.dto.AlbumDto;
import com.example.spotify.entity.Album;
import com.example.spotify.repository.AlbumRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public List<AlbumDto> getPopularAlbums() {
        return albumRepository.findTop10Album()
                .stream()
                .map(album -> AlbumDto.builder()
                        .id(album.getId())
                        .imageUrl(album.getImageUrl())
                        .title(album.getTitle())
                        .artistName(album.getArtistName())
                        .build()).toList();
    }

    public List<Album> getAlbumsByArtistId(Long artistId) {
        return albumRepository.findAlbumsByArtistId(artistId);
    }
}
