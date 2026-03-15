package com.example.spotify.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    private Integer genreId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public Integer getGenreId() { return genreId; }
    public String getName() { return name; }

    public void setGenreId(Integer genreId) { this.genreId = genreId; }
    public void setName(String name) { this.name = name; }
}
