package com.example.spotify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class TrackSearchItemDto {
    private Long trackId;
    private String title;
    private Integer duration;
    private String audioUrl;
    private String imageUrl;
    private Long playCount;
    private String albumTitle;
    private List<String> artists;
    private List<String> genres;

    public TrackSearchItemDto() {}

    public Long getTrackId() { return trackId; }
    public void setTrackId(Long trackId) { this.trackId = trackId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Long getPlayCount() { return playCount; }
    public void setPlayCount(Long playCount) { this.playCount = playCount; }
    public String getAlbumTitle() { return albumTitle; }
    public void setAlbumTitle(String albumTitle) { this.albumTitle = albumTitle; }
    public List<String> getArtists() { return artists; }
    public void setArtists(List<String> artists) { this.artists = artists; }
    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    @JsonProperty("track_id")
    public Long getTrackIdSnakeCase() {
        return trackId;
    }

    @JsonProperty("image_url")
    public String getImageUrlSnakeCase() {
        return imageUrl;
    }

    @JsonProperty("artist_name")
    public String getArtistName() {
        if (artists == null || artists.isEmpty()) {
            return null;
        }
        return artists.stream()
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.joining(", "));
    }
}
