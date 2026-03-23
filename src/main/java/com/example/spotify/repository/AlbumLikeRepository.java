package com.example.spotify.repository;

import com.example.spotify.entity.AlbumLike;
import com.example.spotify.entity.AlbumLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumLikeRepository extends JpaRepository<AlbumLike, AlbumLikeId> {

    // 1. Kiểm tra xem User đã thích Album này chưa (Truyền thẳng AlbumLikeId)
    @Query("SELECT CASE WHEN COUNT(al) > 0 THEN true ELSE false END FROM AlbumLike al WHERE al.id = :id")
    boolean checkExistsById(@Param("id") AlbumLikeId id);

    // Hoặc nếu bạn muốn truyền userId và albumId rời nhau cho tiện lợi hơn:
    @Query("SELECT CASE WHEN COUNT(al) > 0 THEN true ELSE false END FROM AlbumLike al WHERE al.user.userId = :userId AND al.album.albumId = :albumId")
    boolean existsByUserIdAndAlbumId(@Param("userId") Long userId, @Param("albumId") Long albumId);

    // 2. Lấy danh sách album đã thích sắp xếp theo thời gian mới nhất
    @Query("SELECT al FROM AlbumLike al WHERE al.user.userId = :userId ORDER BY al.likedAt DESC")
    List<AlbumLike> findLikedAlbumsByUser(@Param("userId") Long userId);
}
