package com.example.spotify.service;

import com.example.spotify.entity.Album;
import com.example.spotify.entity.AlbumLike;
import com.example.spotify.entity.AlbumLikeId;
import com.example.spotify.entity.User;
import com.example.spotify.repository.AlbumLikeRepository;
import com.example.spotify.repository.AlbumRepository;
import com.example.spotify.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumLikeService {

    @Autowired
    private AlbumLikeRepository albumLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlbumRepository albumRepository;

    // 1. Kiểm tra album có được thích không
    public boolean isAlbumLiked(Long userId, Long albumId) {
        AlbumLikeId id = new AlbumLikeId(userId, albumId);
        return albumLikeRepository.existsById(id);
    }

    // 2. Thích album
    @Transactional
    public void likeAlbum(Long userId, Long albumId) {
        AlbumLikeId id = new AlbumLikeId(userId, albumId);

        // Tránh lỗi duplicate nếu đã thích rồi
        if (albumLikeRepository.existsById(id)) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        AlbumLike albumLike = new AlbumLike();
        albumLike.setId(id);
        albumLike.setUser(user);
        albumLike.setAlbum(album);
        albumLike.setLikedAt(LocalDateTime.now());

        albumLikeRepository.save(albumLike);
    }

    // 3. Hủy thích album
    @Transactional
    public void unlikeAlbum(Long userId, Long albumId) {
        AlbumLikeId id = new AlbumLikeId(userId, albumId);

        if (albumLikeRepository.existsById(id)) {
            albumLikeRepository.deleteById(id);
        }
    }

    // 4. Lấy danh sách các Album đã thích
    public List<Album> getLikedAlbums(Long userId) {
        List<AlbumLike> likedRecords = albumLikeRepository.findLikedAlbumsByUser(userId);

        // Map từ entity AlbumLike sang entity Album
        return likedRecords.stream()
                .map(AlbumLike::getAlbum)
                .collect(Collectors.toList());
    }
}
