package com.example.spotify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistTrackItemDto {
    private Long trackId;
    private String title;
    private String imageUrl;
    private String audioUrl;
    private String artistName;
}
