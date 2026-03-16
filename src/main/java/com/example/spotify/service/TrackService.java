package com.example.spotify.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.spotify.entity.Track;
import com.example.spotify.repository.TrackRepository;
import org.springframework.data.domain.Page;

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

}
