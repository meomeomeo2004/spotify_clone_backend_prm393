package com.example.spotify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.spotify.entity.Track;

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

}
