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
public class AlbumArtistId implements Serializable {
    private static final long serialVersionUID = -1214261168993055087L;
    @Column(name = "album_id", nullable = false)
    private Long albumId;

    @Column(name = "artist_id", nullable = false)
    private Long artistId;


}