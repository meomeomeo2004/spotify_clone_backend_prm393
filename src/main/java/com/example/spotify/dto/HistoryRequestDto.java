package com.example.spotify.dto;

import lombok.*;

@Builder
@Getter
@Setter
public class HistoryRequestDto {
    private Long userId;
    private Long trackId;

    public HistoryRequestDto(Long userId, Long trackId) {
        this.userId = userId;
        this.trackId = trackId;
    }
}

