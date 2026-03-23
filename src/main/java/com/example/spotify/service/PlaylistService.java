package com.example.spotify.service;

import com.example.spotify.dto.AddTrackToPlaylistRequest;
import com.example.spotify.dto.CreatePlaylistRequest;
import com.example.spotify.dto.PlaylistDetailDto;
import com.example.spotify.dto.PlaylistDto;
import com.example.spotify.dto.PlaylistTrackItemDto;
import com.example.spotify.entity.Playlist;
import com.example.spotify.entity.PlaylistTrack;
import com.example.spotify.entity.PlaylistTrackId;
import com.example.spotify.entity.Track;
import com.example.spotify.entity.User;
import com.example.spotify.repository.PlaylistRepository;
import com.example.spotify.repository.PlaylistTrackRepository;
import com.example.spotify.repository.TrackRepository;
import com.example.spotify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepo;
    private final PlaylistTrackRepository playlistTrackRepo;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;

    /**
     * Returns all playlists belonging to the given user, newest first.
     */
    public List<PlaylistDto> getPlaylistsByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        List<Playlist> playlists = playlistRepo.findByUser_UserIdOrderByCreatedAtDesc(userId);

        return playlists.stream().map(this::toListDto).collect(Collectors.toList());
    }

    private PlaylistDto toListDto(Playlist p) {
        PlaylistDto dto = new PlaylistDto();
        dto.setId(p.getId());
        dto.setName(p.getName() != null ? p.getName() : "Untitled");
        dto.setDescription(p.getDescription() != null ? p.getDescription() : "");

        long count = playlistTrackRepo.countByPlaylistId(p.getId());
        dto.setTrackCount((int) count);

        List<String> images = new ArrayList<>(playlistTrackRepo.findTrackImagesByPlaylistId(p.getId()));
        List<String> nonEmpty = images.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        Collections.shuffle(nonEmpty);
        List<String> collage = nonEmpty.stream().limit(4).collect(Collectors.toList());
        dto.setCoverImageUrls(collage);
        dto.setCoverImageUrl(collage.isEmpty() ? "" : collage.get(0));

        if (p.getCreatedAt() != null) {
            dto.setCreatedAt(p.getCreatedAt().toString());
        }

        return dto;
    }

    @Transactional
    public PlaylistDto createPlaylist(CreatePlaylistRequest req) {
        if (req == null || req.getUserId() == null || req.getUserId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId");
        }
        String name = req.getName() != null ? req.getName().trim() : "";
        if (name.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Playlist name is required");
        }

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Playlist p = new Playlist();
        p.setUser(user);
        p.setName(name);
        p.setDescription(req.getDescription() != null ? req.getDescription().trim() : "");
        LocalDateTime now = LocalDateTime.now();
        p.setCreatedAt(now);
        p.setUpdatedAt(now);

        p = playlistRepo.save(p);
        return toListDto(p);
    }

    public PlaylistDetailDto getPlaylistDetail(Long playlistId, Long userId) {
        if (playlistId == null || playlistId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid playlistId");
        }
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId");
        }

        Playlist playlist = playlistRepo.findById(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));

        if (playlist.getUser() == null || !userId.equals(playlist.getUser().getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this playlist");
        }

        PlaylistDetailDto dto = new PlaylistDetailDto();
        dto.setId(playlist.getId());
        dto.setName(playlist.getName() != null ? playlist.getName() : "Untitled");
        dto.setDescription(playlist.getDescription() != null ? playlist.getDescription() : "");

        List<Object[]> rows = playlistTrackRepo.findTrackRowsForPlaylist(playlistId);
        List<PlaylistTrackItemDto> tracks = new ArrayList<>();
        for (Object[] row : rows) {
            Long trackId = row[0] != null ? ((Number) row[0]).longValue() : null;
            String title = row[1] != null ? row[1].toString() : "";
            String imageUrl = row[2] != null ? row[2].toString() : "";
            String audioUrl = row[3] != null ? row[3].toString() : "";
            String artistName = row[4] != null ? row[4].toString() : "Unknown";
            if (trackId != null) {
                tracks.add(new PlaylistTrackItemDto(trackId, title, imageUrl, audioUrl, artistName));
            }
        }
        dto.setTracks(tracks);
        dto.setTrackCount(tracks.size());

        List<String> collage = tracks.stream()
                .map(PlaylistTrackItemDto::getImageUrl)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        Collections.shuffle(collage);
        dto.setCoverImageUrls(collage.stream().limit(4).collect(Collectors.toList()));

        return dto;
    }

    @Transactional
    public PlaylistDetailDto addTrackToPlaylist(Long playlistId, AddTrackToPlaylistRequest req) {
        if (req == null || req.getUserId() == null || req.getUserId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId");
        }
        if (req.getTrackId() == null || req.getTrackId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid trackId");
        }

        Playlist playlist = playlistRepo.findById(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));

        if (playlist.getUser() == null || !req.getUserId().equals(playlist.getUser().getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this playlist");
        }

        Track track = trackRepository.findById(req.getTrackId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Track not found"));

        if (playlistTrackRepo.existsByPlaylistIdAndTrackId(playlistId, req.getTrackId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Track already in playlist");
        }

        PlaylistTrackId id = new PlaylistTrackId();
        id.setPlaylistId(playlistId);
        id.setTrackId(req.getTrackId());

        PlaylistTrack pt = new PlaylistTrack();
        pt.setId(id);
        pt.setPlaylist(playlist);
        pt.setTrack(track);
        pt.setAddedAt(LocalDateTime.now());

        playlistTrackRepo.save(pt);
        playlist.setUpdatedAt(LocalDateTime.now());
        playlistRepo.save(playlist);

        return getPlaylistDetail(playlistId, req.getUserId());
    }

    @Transactional
    public PlaylistDetailDto removeTrackFromPlaylist(Long playlistId, Long trackId, Long userId) {
        if (playlistId == null || playlistId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid playlistId");
        }
        if (trackId == null || trackId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid trackId");
        }
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId");
        }

        Playlist playlist = playlistRepo.findById(playlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));

        if (playlist.getUser() == null || !userId.equals(playlist.getUser().getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this playlist");
        }

        int deleted = playlistTrackRepo.deleteByPlaylistIdAndTrackId(playlistId, trackId);
        if (deleted == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Track not in playlist");
        }

        playlist.setUpdatedAt(LocalDateTime.now());
        playlistRepo.save(playlist);

        return getPlaylistDetail(playlistId, userId);
    }
}
