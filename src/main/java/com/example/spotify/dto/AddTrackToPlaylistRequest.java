package com.example.spotify.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddTrackToPlaylistRequest {
    private Long userId;
    private Long trackId;
}
