package com.example.spotify.controller;

import com.example.spotify.dto.CommentDto;
import com.example.spotify.dto.CreateCommentRequest;
import com.example.spotify.dto.ReactCommentRequest;
import com.example.spotify.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST endpoints for track comments.
 *
 *   GET  /api/comments/track/{trackId}?userId={userId}   — list comments
 *   POST /api/comments                                    — create / reply
 *   POST /api/comments/{commentId}/react                  — like / dislike
 */
@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ── GET /api/comments/track/{trackId} ────────────────────────────────────
    @GetMapping("/track/{trackId}")
    public ResponseEntity<List<CommentDto>> getByTrack(
            @PathVariable Long trackId,
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(commentService.getCommentsByTrackId(trackId, userId));
    }

    // ── POST /api/comments ───────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateCommentRequest req) {
        if (req.getContent() == null || req.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Content cannot be empty"));
        }
        if (req.getUserId() == null || req.getTrackId() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "userId and trackId are required"));
        }
        try {
            CommentDto created = commentService.createComment(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ── POST /api/comments/{commentId}/react ─────────────────────────────────
    @PostMapping("/{commentId}/react")
    public ResponseEntity<?> react(
            @PathVariable Long commentId,
            @RequestBody ReactCommentRequest req) {
        if (req.getUserId() == null || req.getReactionType() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "userId and reactionType are required"));
        }
        try {
            CommentDto updated = commentService.reactToComment(commentId, req);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
