package com.example.spotify.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentDto {
    private Long id;
    private String content;
    /** ISO-8601 string, e.g. "2026-03-22T10:05:00". Null-safe for old rows. */
    private String createdAt;
    private Long userId;
    private String username;
    private int likeCount;
    private int dislikeCount;
    /** "like" | "dislike" | null — current requesting user's reaction */
    private String myReaction;
    /** null for top-level comments */
    private Long parentCommentId;
    /** Populated one level deep (replies to replies are not nested further) */
    private List<CommentDto> replies;
}
