package com.example.spotify.repository;

import com.example.spotify.entity.CommentReaction;
import com.example.spotify.entity.CommentReactionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReactionRepository
        extends JpaRepository<CommentReaction, CommentReactionId> {

    /** How many reactions of a given type exist for a comment. */
    @Query("SELECT COUNT(cr) FROM CommentReaction cr " +
           "WHERE cr.comment.id = :commentId AND cr.reactionType = :type")
    long countByCommentIdAndType(@Param("commentId") Long commentId,
                                 @Param("type") String type);
}
