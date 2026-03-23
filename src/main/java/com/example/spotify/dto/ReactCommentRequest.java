package com.example.spotify.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReactCommentRequest {
    private Long userId;
    /** "like" or "dislike" */
    private String reactionType;
}
