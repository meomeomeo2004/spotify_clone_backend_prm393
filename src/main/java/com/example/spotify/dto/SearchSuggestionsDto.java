package com.example.spotify.dto;

import java.util.List;

public class SearchSuggestionsDto {
    private List<String> tracks;
    private List<String> artists;
    private List<String> genres;

    public SearchSuggestionsDto(List<String> tracks, List<String> artists, List<String> genres) {
        this.tracks = tracks;
        this.artists = artists;
        this.genres = genres;
    }

    public List<String> getTracks() { return tracks; }
    public List<String> getArtists() { return artists; }
    public List<String> getGenres() { return genres; }

    public void setTracks(List<String> tracks) { this.tracks = tracks; }
    public void setArtists(List<String> artists) { this.artists = artists; }
    public void setGenres(List<String> genres) { this.genres = genres; }
}
