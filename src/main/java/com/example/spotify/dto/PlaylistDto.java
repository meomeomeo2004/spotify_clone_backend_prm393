package com.example.spotify.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlaylistDto {
    private Long id;
    private String name;
    private String description;
    /** Image URL of the first track added to the playlist; empty string if none. */
    private String coverImageUrl;
    private int trackCount;
}
