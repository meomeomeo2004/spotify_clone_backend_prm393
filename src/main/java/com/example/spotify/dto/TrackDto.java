package com.example.spotify.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TrackDto {
    private Long id;
    private String imageUrl;
    private String title;
    private String artistName;
    private String audioUrl;

    public TrackDto(Long id, String imageUrl, String title, String artistName, String audioUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.artistName = artistName;
        this.audioUrl = audioUrl;
    }
}
