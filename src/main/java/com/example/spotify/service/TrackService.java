package com.example.spotify.service;

import java.util.Optional;

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
        Optional<Track> movieOptional = trackRepository.findById(id);// do findById trả về Optional để tránh lỗi
                                                                     // NullPointerException khi không tìm thấy movie
                                                                     // với id đó
        if (movieOptional.isPresent()) {
            return movieOptional.get();// nếu movie tồn tại, trả về movie đó
        }
        return null;
    }

}
