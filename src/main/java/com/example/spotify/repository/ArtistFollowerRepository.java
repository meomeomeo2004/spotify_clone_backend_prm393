package com.example.spotify.repository;

import com.example.spotify.dto.FollowingArtistDto;
import com.example.spotify.entity.ArtistFollower;
import com.example.spotify.entity.ArtistFollowerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistFollowerRepository extends JpaRepository<ArtistFollower, ArtistFollowerId> {

    /**
     * Đếm số artist mà một user đang follow.
     * SQL tương đương: SELECT COUNT(*) FROM artist_followers WHERE user_id = :userId
     */
    @Query("SELECT COUNT(af) FROM ArtistFollower af WHERE af.id.userId = :userId")
    long countFollowingByUserId(@Param("userId") Long userId);

    /**
     * Lấy danh sách artist mà user đang follow, kèm tổng follower count toàn cầu của từng artist.
     * - JOIN vào artist thông qua af.artist
     * - Subquery đếm tất cả row trong artist_followers có cùng artist_id (global follower count)
     * - ORDER BY followed_at ASC (Oldest first)
     */
    @Query("SELECT new com.example.spotify.dto.FollowingArtistDto(" +
            "a.id, a.name, a.imageUrl, " +
            "(SELECT COUNT(af2) FROM ArtistFollower af2 WHERE af2.id.artistId = a.id)) " +
            "FROM ArtistFollower af " +
            "JOIN af.artist a " +
            "WHERE af.id.userId = :userId " +
            "ORDER BY af.followedAt ASC")
    List<FollowingArtistDto> findFollowingArtistsByUserId(@Param("userId") Long userId);
}
