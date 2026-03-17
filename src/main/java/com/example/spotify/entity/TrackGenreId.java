package com.example.spotify.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class TrackGenreId implements Serializable {
    private static final long serialVersionUID = -7575122240816768507L;
    @Column(name = "track_id", nullable = false)
    private Long trackId;

    @Column(name = "genre_id", nullable = false)
    private Integer genreId;


}