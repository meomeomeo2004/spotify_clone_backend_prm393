package com.example.spotify.dto;

public class GenreDto {
    private Integer genreId;
    private String name;

    public GenreDto(Integer genreId, String name) {
        this.genreId = genreId;
        this.name = name;
    }

    public Integer getGenreId() { return genreId; }
    public String getName() { return name; }

    public void setGenreId(Integer genreId) { this.genreId = genreId; }
    public void setName(String name) { this.name = name; }
}
