package com.example.spotify.service;
import com.example.spotify.entity.Track;
import com.example.spotify.entity.TrackLike;
import com.example.spotify.entity.TrackLikeId;
import com.example.spotify.entity.User;
import com.example.spotify.repository.TrackLikeRepository;
import com.example.spotify.repository.TrackRepository;
import com.example.spotify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TrackLikeService {
    private final TrackLikeRepository trackLikeRepository;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;

    // 1. Thêm bài hát vào danh sách yêu thích
    @Transactional
    public void likeTrack(Long userId, Long trackId) {

        if (trackLikeRepository.findByUserIdAndTrackId(userId, trackId).isPresent()) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        TrackLikeId trackLikeId = new TrackLikeId(userId, trackId);

        TrackLike trackLike = new TrackLike();
        trackLike.setId(trackLikeId);
        trackLike.setUser(user);
        trackLike.setTrack(track);
        trackLike.setLikedAt(LocalDateTime.now());

        trackLikeRepository.save(trackLike);
    }

    // 2. Xóa bài hát khỏi danh sách yêu thích
    @Transactional
    public void unlikeTrack(Long userId, Long trackId) {
        trackLikeRepository.deleteByUserIdAndTrackId(userId, trackId);
    }

    // 3. Lấy danh sách bài hát yêu thích của user
    @Transactional(readOnly = true)
    public List<Track> getLikedTracksByUser(Long userId) {
        List<TrackLike> trackLikes = trackLikeRepository.findAllByUserId(userId);

        return trackLikes.stream()
                .map(TrackLike::getTrack)
                .collect(Collectors.toList());
    }
    public boolean isLiked(Long userId, Long trackId) {
        return trackLikeRepository.checkIfUserLikedTrack(userId, trackId);
    }
}
