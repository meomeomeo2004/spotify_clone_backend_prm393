package com.example.spotify.repository;

import com.example.spotify.dto.TrackDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.spotify.entity.Track;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

    // Lấy bài tiếp theo
    @Query(value = "SELECT * FROM tracks WHERE track_id > :currentId ORDER BY track_id ASC LIMIT 1", nativeQuery = true)
    Track findNextTrack(@Param("currentId") Long currentId);

    // Lấy bài đầu tiên (dùng để quay vòng khi hết list)
    @Query(value = "SELECT * FROM tracks ORDER BY track_id ASC LIMIT 1", nativeQuery = true)
    Track findFirstTrack();

    // Lấy bài trước đó
    @Query(value = "SELECT * FROM tracks WHERE track_id < :currentId ORDER BY track_id DESC LIMIT 1", nativeQuery = true)
    Track findPreviousTrack(@Param("currentId") Long currentId);

    // Lấy bài cuối cùng (dùng để quay vòng về cuối khi đang ở bài 1 mà ấn lùi)
    @Query(value = "SELECT * FROM tracks ORDER BY track_id DESC LIMIT 1", nativeQuery = true)
    Track findLastTrack();

    @Query("SELECT new com.example.spotify.dto.TrackDto(t.trackId, t.imageUrl ,t.title, a.name, t.audioUrl) " +
            "FROM Track t Join TrackArtist ta on t.trackId = ta.track.trackId " +
            "join Artist a on ta.artist.id = a.id ORDER BY RAND() LIMIT 10")
    List<TrackDto> findRandom10Track();

    @Query("SELECT new com.example.spotify.dto.TrackDto(t.trackId, t.imageUrl ,t.title, a.name, t.audioUrl) " +
            "FROM Track t join Album al ON t.album.albumId = al.albumId " +
            "Join TrackArtist ta on t.trackId = ta.track.trackId " +
            "JOIN Artist a on ta.artist.id = a.id " +
            "WHERE al.albumId = :id ")
    List<TrackDto> findTrackByAlbumId(@Param("id") Long id);

    @Query("SELECT new com.example.spotify.dto.TrackDto(t.trackId, t.imageUrl ,t.title, a.name, t.audioUrl) " +
            "FROM Track t JOIN TrackArtist ta ON t.trackId = ta.track.trackId " +
            "JOIN Artist a ON ta.artist.id = a.id " +
            "WHERE a.id = :id ")
    List<TrackDto> findTrackByArtistId(@Param("id") Long id);
}
