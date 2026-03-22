package com.example.spotify.service;

import com.example.spotify.dto.CommentDto;
import com.example.spotify.dto.CreateCommentRequest;
import com.example.spotify.dto.ReactCommentRequest;
import com.example.spotify.entity.*;
import com.example.spotify.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final CommentRepository commentRepo;
    private final CommentReactionRepository reactionRepo;
    private final UserRepository userRepo;
    private final TrackRepository trackRepo;

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns all top-level comments for a track, each with its replies nested
     * one level deep.  If {@code userId} is non-null, also populates
     * {@code myReaction} for the requesting user.
     */
    public List<CommentDto> getCommentsByTrackId(Long trackId, Long userId) {
        return commentRepo.findTopLevelByTrackId(trackId).stream()
                .map(c -> mapToDto(c, userId, true))
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────

    /** Creates a new comment or reply and returns the saved DTO. */
    @Transactional
    public CommentDto createComment(CreateCommentRequest req) {
        String content = req.getContent() == null ? "" : req.getContent().trim();
        if (content.isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }

        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + req.getUserId()));

        Track track = trackRepo.findById(req.getTrackId())
                .orElseThrow(() -> new RuntimeException("Track not found: " + req.getTrackId()));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setTrack(track);
        comment.setContent(content);
        comment.setIsDeleted(false);
        comment.setIsEdited(false);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        if (req.getParentCommentId() != null) {
            Comment parent = commentRepo.findById(req.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException(
                            "Parent comment not found: " + req.getParentCommentId()));
            comment.setParentComment(parent);
        }

        Comment saved = commentRepo.save(comment);
        return mapToDto(saved, req.getUserId(), false);
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Toggles a like/dislike reaction on a comment.
     * Rules:
     *   - Same reaction again → remove (toggle off)
     *   - Opposite reaction    → switch
     *   - No prior reaction    → create
     */
    @Transactional
    public CommentDto reactToComment(Long commentId, ReactCommentRequest req) {
        String rType = req.getReactionType();
        if (!"like".equals(rType) && !"dislike".equals(rType)) {
            throw new IllegalArgumentException("reactionType must be 'like' or 'dislike'");
        }

        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found: " + commentId));

        CommentReactionId rId = new CommentReactionId();
        rId.setUserId(req.getUserId());
        rId.setCommentId(commentId);

        Optional<CommentReaction> existing = reactionRepo.findById(rId);

        if (existing.isPresent()) {
            if (existing.get().getReactionType().equals(rType)) {
                reactionRepo.delete(existing.get());          // toggle off
            } else {
                existing.get().setReactionType(rType);
                reactionRepo.save(existing.get());            // switch reaction
            }
        } else {
            User userRef = userRepo.getReferenceById(req.getUserId());
            CommentReaction reaction = new CommentReaction();
            reaction.setId(rId);
            reaction.setUser(userRef);
            reaction.setComment(comment);
            reaction.setReactionType(rType);
            reactionRepo.save(reaction);
        }

        // Return fresh counts (re-read from DB after mutation)
        Comment refreshed = commentRepo.findById(commentId).orElse(comment);
        return mapToDto(refreshed, req.getUserId(), false);
    }

    // ─── Mapping helpers ─────────────────────────────────────────────────────

    private CommentDto mapToDto(Comment c, Long userId, boolean includeReplies) {
        CommentDto dto = new CommentDto();
        dto.setId(c.getId());
        dto.setContent(c.getContent());
        dto.setCreatedAt(c.getCreatedAt() != null
                ? c.getCreatedAt().format(DT_FMT) : "");
        dto.setUserId(c.getUser().getUserId());
        dto.setUsername(c.getUser().getUsername());
        dto.setLikeCount((int) reactionRepo.countByCommentIdAndType(c.getId(), "like"));
        dto.setDislikeCount((int) reactionRepo.countByCommentIdAndType(c.getId(), "dislike"));
        dto.setParentCommentId(
                c.getParentComment() != null ? c.getParentComment().getId() : null);

        if (userId != null) {
            CommentReactionId rId = new CommentReactionId();
            rId.setUserId(userId);
            rId.setCommentId(c.getId());
            reactionRepo.findById(rId)
                        .ifPresent(r -> dto.setMyReaction(r.getReactionType()));
        }

        if (includeReplies) {
            List<CommentDto> replies = commentRepo.findRepliesByParentId(c.getId())
                    .stream()
                    .map(r -> mapToDto(r, userId, false))
                    .collect(Collectors.toList());
            dto.setReplies(replies);
        } else {
            dto.setReplies(Collections.emptyList());
        }

        return dto;
    }
}
