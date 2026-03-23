package com.example.spotify.repository;

import com.example.spotify.entity.PlaylistTrack;
import com.example.spotify.entity.PlaylistTrackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistTrackRepository
        extends JpaRepository<PlaylistTrack, PlaylistTrackId> {

    /** How many tracks are in a playlist. */
    @Query("SELECT COUNT(pt) FROM PlaylistTrack pt WHERE pt.id.playlistId = :playlistId")
    long countByPlaylistId(@Param("playlistId") Long playlistId);

    /** Image URLs of all tracks in a playlist, ordered by when they were added. */
    @Query("SELECT pt.track.imageUrl FROM PlaylistTrack pt " +
           "WHERE pt.id.playlistId = :playlistId ORDER BY pt.addedAt ASC")
    List<String> findTrackImagesByPlaylistId(@Param("playlistId") Long playlistId);
}
