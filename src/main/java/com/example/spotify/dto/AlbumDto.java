package com.example.spotify.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class AlbumDto {
    private Long id;
    private String imageUrl;
    private String title;
    private String artistName;

    public AlbumDto(Long id, String imageUrl, String title, String artistName) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.artistName = artistName;
    }
}