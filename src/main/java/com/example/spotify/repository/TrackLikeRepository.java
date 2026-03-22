package com.example.spotify.repository;
import java.util.List;
import java.util.Optional;

import com.example.spotify.entity.TrackLike;
import com.example.spotify.entity.TrackLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackLikeRepository extends JpaRepository<TrackLike, TrackLikeId> {

    // Tìm kiếm xem user đã like bài hát này chưa
    @Query("SELECT tl FROM TrackLike tl WHERE tl.user.userId = :userId AND tl.track.trackId = :trackId")
    Optional<TrackLike> findByUserIdAndTrackId(@Param("userId") Long userId, @Param("trackId") Long trackId);

    @Query("SELECT tl FROM TrackLike tl WHERE tl.user.userId = :userId")
    List<TrackLike> findAllByUserId(@Param("userId") Long userId);
    @Query("SELECT COUNT(tl) > 0 FROM TrackLike tl WHERE tl.user.userId = :userId AND tl.track.trackId = :trackId")
    boolean checkIfUserLikedTrack(@Param("userId") Long userId, @Param("trackId") Long trackId);

    @Modifying
    @Query("DELETE FROM TrackLike tl WHERE tl.user.userId = :userId AND tl.track.trackId = :trackId")
    void deleteByUserIdAndTrackId(@Param("userId") Long userId, @Param("trackId") Long trackId);
}
