package com.example.spotify.repository;

import com.example.spotify.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    /** All playlists belonging to a user, newest first. */
    List<Playlist> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
}
