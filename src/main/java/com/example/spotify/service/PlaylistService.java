package com.example.spotify.service;

import com.example.spotify.dto.PlaylistDto;
import com.example.spotify.entity.Playlist;
import com.example.spotify.repository.PlaylistRepository;
import com.example.spotify.repository.PlaylistTrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepo;
    private final PlaylistTrackRepository playlistTrackRepo;

    /**
     * Returns all playlists belonging to the given user, newest first.
     * Each DTO includes the track count and the first-track cover image URL.
     */
    public List<PlaylistDto> getPlaylistsByUserId(Long userId) {
        if (userId == null) return Collections.emptyList();

        List<Playlist> playlists = playlistRepo.findByUser_UserIdOrderByCreatedAtDesc(userId);

        return playlists.stream().map(p -> {
            PlaylistDto dto = new PlaylistDto();
            dto.setId(p.getId());
            dto.setName(p.getName() != null ? p.getName() : "Untitled");
            dto.setDescription(p.getDescription() != null ? p.getDescription() : "");

            long count = playlistTrackRepo.countByPlaylistId(p.getId());
            dto.setTrackCount((int) count);

            // Use the first track's image as the playlist cover
            List<String> images = playlistTrackRepo.findTrackImagesByPlaylistId(p.getId());
            dto.setCoverImageUrl(images.isEmpty() ? "" : (images.get(0) != null ? images.get(0) : ""));

            return dto;
        }).collect(Collectors.toList());
    }
}
