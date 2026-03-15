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
public class PlaylistTrackId implements Serializable {
    private static final long serialVersionUID = 4789097993658540846L;
    @Column(name = "playlist_id", nullable = false)
    private Long playlistId;

    @Column(name = "track_id", nullable = false)
    private Long trackId;


}