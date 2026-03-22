package com.example.spotify.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequest {
    private Long trackId;
    private Long userId;
    private String content;
    /** Null for new top-level comments; set to parent id when replying */
    private Long parentCommentId;
}
