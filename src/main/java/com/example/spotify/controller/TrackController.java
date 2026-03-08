package com.example.spotify.controller;
import com.example.spotify.entity.Track;
// import com.example.spotify.repository.TrackRepository;
import com.example.spotify.service.TrackService;

// import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin // cho phép Flutter gọi
public class TrackController {
    private final TrackService trackService;
    public TrackController(TrackService trackService){
        this.trackService = trackService;
    }


    @GetMapping("/tracks")
    public List<Track> getAllTracks() {
        List<Track> getAllTrack = trackService.getAllTracks();
        return getAllTrack;
    }
}