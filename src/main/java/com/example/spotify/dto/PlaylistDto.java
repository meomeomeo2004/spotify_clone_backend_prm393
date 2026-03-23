package com.example.spotify.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PlaylistDto {
    private Long id;
    private String name;
    private String description;
    /** Primary cover (first of collage); empty string if none. */
    private String coverImageUrl;
    /** Up to 4 non-empty track image URLs, shuffled for collage UI. */
    private List<String> coverImageUrls = new ArrayList<>();
    private int trackCount;
    /** ISO-8601 local date-time from DB for sorting (optional for older clients). */
    private String createdAt;
}
