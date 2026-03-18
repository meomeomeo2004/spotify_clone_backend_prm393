package com.example.spotify.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUsernameRequest {
    private Long userId;
    private String username;
}
