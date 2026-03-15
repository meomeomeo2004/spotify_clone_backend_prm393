package com.example.spotify.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "track_genres")
public class TrackGenre {
    @EmbeddedId
    private TrackGenreId id;

    @MapsId("trackId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    @MapsId("genreId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;


}