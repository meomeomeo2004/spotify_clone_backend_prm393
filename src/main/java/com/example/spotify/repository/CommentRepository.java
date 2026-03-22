package com.example.spotify.repository;

import com.example.spotify.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /** Top-level comments for a track (no parent), newest first. */
    @Query("SELECT c FROM Comment c " +
           "WHERE c.track.trackId = :trackId " +
           "AND c.parentComment IS NULL " +
           "AND (c.isDeleted IS NULL OR c.isDeleted = false) " +
           "ORDER BY c.createdAt DESC")
    List<Comment> findTopLevelByTrackId(@Param("trackId") Long trackId);

    /** Replies for a given parent comment, oldest first. */
    @Query("SELECT c FROM Comment c " +
           "WHERE c.parentComment.id = :parentId " +
           "AND (c.isDeleted IS NULL OR c.isDeleted = false) " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParentId(@Param("parentId") Long parentId);
}
