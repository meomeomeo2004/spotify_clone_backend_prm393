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
public class TrackArtistId implements Serializable {
    private static final long serialVersionUID = -1646070115677743270L;
    @Column(name = "track_id", nullable = false)
    private Long trackId;

    @Column(name = "artist_id", nullable = false)
    private Long artistId;


}