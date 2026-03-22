package com.example.spotify.service;

import java.util.List;
import java.util.Optional;
import com.example.spotify.dto.TrackDto;
import org.springframework.stereotype.Service;
import com.example.spotify.entity.Track;
import com.example.spotify.repository.TrackRepository;

@Service
public class TrackService {
    private final TrackRepository trackRepository;

    public TrackService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    public Track getTrackByID(Long id) {
        Optional<Track> trackOptional = trackRepository.findById(id);// do findById trả về Optional để tránh lỗi
                                                                     // NullPointerException khi không tìm thấy movie
                                                                     // với id đó
        if (trackOptional.isPresent()) {
            return trackOptional.get();// nếu movie tồn tại, trả về movie đó
        }
        return null;
    }

    public Track getNextTrack(Long currentId) {
        if (currentId == null) {
            return trackRepository.findFirstTrack();
        }
        Track nextTrack = trackRepository.findNextTrack(currentId);
        if (nextTrack == null) {
            return trackRepository.findFirstTrack();
        }
        return nextTrack;
    }

    public Track getPreviousTrack(Long currentId) {
        if (currentId == null) {
            return trackRepository.findLastTrack(); // Nếu lỗi không có ID, trả về bài cuối
        }
        Track previousTrack = trackRepository.findPreviousTrack(currentId);
        if (previousTrack == null) {
            // Nếu đang ở bài 1 mà bấm lùi (không có bài nào ID nhỏ hơn), quay vòng về bài
            // cuối
            return trackRepository.findLastTrack();
        }
        return previousTrack;
    }
  
    public String getStreamUrlByTrackId(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy track_id = " + trackId));
              if (track.getAudioUrl() == null || track.getAudioUrl().isBlank()) {
            throw new RuntimeException("Track không có audio_url, track_id = " + trackId);
        }
        return track.getAudioUrl();
    }
      

    public List<TrackDto> getRandomTracks() {
        return trackRepository.findRandom10Track()
                .stream()
                .map(track -> TrackDto.builder()
                        .id(track.getId())
                        .imageUrl(track.getImageUrl())
                        .title(track.getTitle())
                        .artistName(track.getArtistName())
                        .audioUrl(track.getAudioUrl())
                        .build()).toList();
    }

    public List<TrackDto> getTracksByAlbumId(Long id) {
        return trackRepository.findTrackByAlbumId(id)
                .stream()
                .map(track -> TrackDto.builder()
                        .id(track.getId())
                        .imageUrl(track.getImageUrl())
                        .title(track.getTitle())
                        .artistName(track.getArtistName())
                        .audioUrl(track.getAudioUrl())
                        .build()).toList();
    }

    public List<TrackDto> getTracksByArtistId(Long id) {
        return trackRepository.findTrackByArtistId(id)
                .stream()
                .map(track -> TrackDto.builder()
                        .id(track.getId())
                        .imageUrl(track.getImageUrl())
                        .title(track.getTitle())
                        .artistName(track.getArtistName())
                        .audioUrl(track.getAudioUrl())
                        .build()).toList();
    }



}
