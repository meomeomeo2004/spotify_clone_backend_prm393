package com.example.spotify.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowingArtistDto {
    private Long id;
    private String name;
    private String imageUrl;
    private long followerCount;

    public FollowingArtistDto(Long id, String name, String imageUrl, long followerCount) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.followerCount = followerCount;
    }
}
