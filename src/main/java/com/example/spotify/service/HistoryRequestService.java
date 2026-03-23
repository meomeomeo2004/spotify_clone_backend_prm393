package com.example.spotify.service;

import com.example.spotify.dto.TrackDto;
import com.example.spotify.dto.TrackHistoryDto;
import com.example.spotify.entity.ListeningHistory;
import com.example.spotify.entity.Track;
import com.example.spotify.entity.User;
import com.example.spotify.repository.HistoryRequestRepository;
import com.example.spotify.repository.TrackRepository;
import com.example.spotify.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoryRequestService {

    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final HistoryRequestRepository historyRepository;

    public HistoryRequestService(TrackRepository trackRepository, UserRepository userRepository, HistoryRequestRepository historyRepository) {
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    public void saveListeningHistory(Long userId, Long trackId) {
        ListeningHistory history = new ListeningHistory();
        User user = userRepository.getReferenceById(userId);
        Track track = trackRepository.getReferenceById(trackId);
        history.setUser(user);
        history.setTrack(track);
        history.setPlayedAt(LocalDateTime.now());
        historyRepository.save(history);
    }

    public List<TrackHistoryDto> getHistoryGrouped(Long userId) {
        return historyRepository.getHistoryGrouped(userId)
                .stream()
                .map(track -> TrackHistoryDto.builder()
                        .id(track.getId())
                        .imageUrl(track.getImageUrl())
                        .title(track.getTitle())
                        .artistName(track.getArtistName())
                        .playedAt(track.getPlayedAt())
                        .build()).toList();
    }
}
