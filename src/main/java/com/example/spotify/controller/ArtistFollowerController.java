package com.example.spotify.controller;

import com.example.spotify.dto.ArtistFollowerDto;
import com.example.spotify.dto.FollowingArtistDto;
import com.example.spotify.service.ArtistFollowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/choose")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ArtistFollowerController {

    private final ArtistFollowerService artistFollowerService;

    /** POST /api/choose/follow-artists — bulk follow (onboarding). */
    @PostMapping("/follow-artists")
    public ResponseEntity<?> followArtists(@RequestBody ArtistFollowerDto request) {
        try {
            artistFollowerService.saveUserArtists(request.getUserId(), request.getArtistIds());
            return ResponseEntity.ok().body("Message: Artists followed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: Failed to follow artists");
        }
    }

    /**
     * GET /api/choose/following-count?userId={userId}
     * Trả về số lượng artist mà user đang follow.
     * Response: { "count": 36 }
     */
    @GetMapping("/following-count")
    public ResponseEntity<?> getFollowingCount(@RequestParam Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid userId"));
        }
        long count = artistFollowerService.countFollowingArtists(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * GET /api/choose/following-artists?userId={userId}
     * Trả về danh sách artist mà user đang follow, kèm follower count toàn cầu của mỗi artist.
     * Response: [ { "id": 1, "name": "...", "imageUrl": "...", "followerCount": 1000 }, ... ]
     */
    @GetMapping("/following-artists")
    public ResponseEntity<?> getFollowingArtists(@RequestParam Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid userId"));
        }
        List<FollowingArtistDto> artists = artistFollowerService.getFollowingArtists(userId);
        return ResponseEntity.ok(artists);
    }
    @GetMapping("/{artistId}/check-follow")
    public ResponseEntity<Boolean> checkFollowStatus(
            @PathVariable Long artistId,
            @RequestParam Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().body(false);
        }
        boolean isFollowed = artistFollowerService.isArtistFollowed(userId, artistId);
        return ResponseEntity.ok(isFollowed);
    }

    @PostMapping("/{artistId}/follow")
    public ResponseEntity<?> followSingleArtist(
            @PathVariable Long artistId,
            @RequestParam Long userId) {
        try {
            artistFollowerService.followSingleArtist(userId, artistId);
            return ResponseEntity.ok(Map.of("message", "Followed artist successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to follow artist"));
        }
    }

    @DeleteMapping("/{artistId}/unfollow")
    public ResponseEntity<?> unfollowArtist(
            @PathVariable Long artistId,
            @RequestParam Long userId) {
        try {
            artistFollowerService.unfollowArtist(userId, artistId);
            return ResponseEntity.ok(Map.of("message", "Unfollowed artist successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to unfollow artist"));
        }
    }

}
