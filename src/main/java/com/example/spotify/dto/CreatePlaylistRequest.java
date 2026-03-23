package com.example.spotify.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreatePlaylistRequest {
    private Long userId;
    private String name;
    private String description;
}
