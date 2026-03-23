package com.example.spotify.repository;

import com.example.spotify.entity.PlaylistTrack;
import com.example.spotify.entity.PlaylistTrackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("SELECT CASE WHEN COUNT(pt) > 0 THEN true ELSE false END " +
            "FROM PlaylistTrack pt " +
            "WHERE pt.id.playlistId = :playlistId AND pt.id.trackId = :trackId")
    boolean existsByPlaylistIdAndTrackId(@Param("playlistId") Long playlistId,
                                         @Param("trackId") Long trackId);

    @Modifying
    @Query("DELETE FROM PlaylistTrack pt WHERE pt.id.playlistId = :playlistId AND pt.id.trackId = :trackId")
    int deleteByPlaylistIdAndTrackId(@Param("playlistId") Long playlistId, @Param("trackId") Long trackId);

    /**
     * Track rows for playlist detail: id, title, image, audio, aggregated artist names (MySQL GROUP_CONCAT).
     */
    @Query(value = """
            SELECT t.track_id, t.title, t.image_url, t.audio_url,
                   (SELECT GROUP_CONCAT(a.name ORDER BY a.name SEPARATOR ', ')
                    FROM track_artists ta2
                    JOIN artists a ON ta2.artist_id = a.artist_id
                    WHERE ta2.track_id = t.track_id) AS artist_names
            FROM playlist_tracks pt
            JOIN tracks t ON pt.track_id = t.track_id
            WHERE pt.playlist_id = :playlistId
            ORDER BY pt.added_at ASC
            """, nativeQuery = true)
    List<Object[]> findTrackRowsForPlaylist(@Param("playlistId") Long playlistId);
}
