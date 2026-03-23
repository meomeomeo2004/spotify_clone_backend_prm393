package com.example.spotify.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class TrackHistoryDto {
    private Long id;
    private String imageUrl;
    private String title;
    private String artistName;
    private LocalDateTime playedAt;

    public TrackHistoryDto(Long id, String imageUrl, String title, String artistName, LocalDateTime playedAt) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.artistName = artistName;
        this.playedAt = playedAt;
    }
}
