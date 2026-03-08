package com.example.spotify.service;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.spotify.entity.Track;
import com.example.spotify.repository.TrackRepository;

@Service
public class TrackService {
    private final TrackRepository trackRepository;
    public TrackService (TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }
    public List<Track> getAllTracks(){
        return this.trackRepository.findAll();
        
    }
}
