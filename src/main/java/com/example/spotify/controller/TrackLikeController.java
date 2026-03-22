package com.example.spotify.controller;
import com.example.spotify.entity.Track;
import com.example.spotify.service.TrackLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users/{userId}/likes")
@RequiredArgsConstructor
public class TrackLikeController {
    private final TrackLikeService trackLikeService;

    // API Like bài hát
    @PostMapping("/{trackId}")
    public ResponseEntity<String> likeTrack(@PathVariable Long userId, @PathVariable Long trackId) {
        trackLikeService.likeTrack(userId, trackId);
        return ResponseEntity.ok("Đã thêm vào danh sách yêu thích");
    }

    // API Unlike bài hát
    @DeleteMapping("/{trackId}")
    public ResponseEntity<String> unlikeTrack(@PathVariable Long userId, @PathVariable Long trackId) {
        trackLikeService.unlikeTrack(userId, trackId);
        return ResponseEntity.ok("Đã xóa khỏi danh sách yêu thích");
    }

    // API Lấy danh sách
    @GetMapping
    public ResponseEntity<List<Track>> getLikedTracks(@PathVariable Long userId) {
        List<Track> likedTracks = trackLikeService.getLikedTracksByUser(userId);
        return ResponseEntity.ok(likedTracks);
    }
    // API Check trạng thái Like
    @GetMapping("/{trackId}/status")
    public ResponseEntity<Boolean> checkLikeStatus(@PathVariable Long userId, @PathVariable Long trackId) {
        boolean isLiked = trackLikeService.isLiked(userId, trackId); // Bạn cần tự implement hàm này trong Service
        return ResponseEntity.ok(isLiked);
    }
}
