package com.example.spotify.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PlaylistDetailDto {
    private Long id;
    private String name;
    private String description;
    private int trackCount;
    private List<String> coverImageUrls = new ArrayList<>();
    private List<PlaylistTrackItemDto> tracks = new ArrayList<>();
}
