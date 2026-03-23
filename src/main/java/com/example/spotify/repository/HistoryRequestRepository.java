package com.example.spotify.repository;

import com.example.spotify.dto.TrackHistoryDto;
import com.example.spotify.entity.ListeningHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRequestRepository extends JpaRepository<ListeningHistory, Integer> {
    @Query("""
        SELECT new com.example.spotify.dto.TrackHistoryDto(t.trackId, t.imageUrl ,t.title, a.name, MAX(lh.playedAt))
        FROM ListeningHistory lh
        JOIN lh.track t
        JOIN t.trackArtists ta
        JOIN ta.artist a
        WHERE lh.user.userId = :userId
        GROUP BY t.trackId, t.imageUrl, t.title, a.name, FUNCTION('DATE', lh.playedAt)
        ORDER BY MAX(lh.playedAt) DESC
    """)
    List<TrackHistoryDto> getHistoryGrouped(@Param("userId") Long userId);
}
