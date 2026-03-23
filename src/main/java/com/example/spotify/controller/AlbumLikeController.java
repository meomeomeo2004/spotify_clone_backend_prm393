package com.example.spotify.controller;

import com.example.spotify.entity.Album;
import com.example.spotify.service.AlbumLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/albums")
public class AlbumLikeController {

    @Autowired
    private AlbumLikeService albumLikeService;

    // 1. Kiểm tra xem User có đang thích Album này không
    @GetMapping("/{albumId}/like-status")
    public ResponseEntity<Boolean> checkLikeStatus(
            @RequestParam Long userId,
            @PathVariable Long albumId) {
        boolean isLiked = albumLikeService.isAlbumLiked(userId, albumId);
        return ResponseEntity.ok(isLiked);
    }

    // 2. Thích Album
    @PostMapping("/{albumId}/like")
    public ResponseEntity<String> likeAlbum(
            @RequestParam Long userId,
            @PathVariable Long albumId) {
        albumLikeService.likeAlbum(userId, albumId);
        return ResponseEntity.ok("Liked successfully");
    }

    // 3. Hủy thích Album
    @DeleteMapping("/{albumId}/unlike")
    public ResponseEntity<String> unlikeAlbum(
            @RequestParam Long userId,
            @PathVariable Long albumId) {
        albumLikeService.unlikeAlbum(userId, albumId);
        return ResponseEntity.ok("Unliked successfully");
    }

    // 4. Lấy danh sách Album yêu thích của User
    @GetMapping("/users/{userId}/liked")
    public ResponseEntity<List<Album>> getLikedAlbums(@PathVariable Long userId) {
        List<Album> likedAlbums = albumLikeService.getLikedAlbums(userId);
        return ResponseEntity.ok(likedAlbums);
    }
}
