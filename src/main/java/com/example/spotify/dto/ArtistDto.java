package com.example.spotify.dto;

import lombok.*;

@Builder
@Getter
@Setter
public class ArtistDto {
    private Long id;
    private String imageUrl;
    private String name;
    private String bio;

    public ArtistDto(Long id, String imageUrl, String name, String bio) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.bio = bio;
    }
}
