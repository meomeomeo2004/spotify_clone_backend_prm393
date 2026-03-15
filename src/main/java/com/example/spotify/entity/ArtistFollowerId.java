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
public class ArtistFollowerId implements Serializable {
    private static final long serialVersionUID = -5233465376364169862L;
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "artist_id", nullable = false)
    private Long artistId;


}