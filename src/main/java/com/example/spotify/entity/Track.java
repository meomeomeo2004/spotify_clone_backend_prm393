package com.example.spotify.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tracks")
@Getter
@Setter
public class Track {

    @Id
    @Column(name = "track_id")
    private Integer trackId;

    private String title;
    private Integer duration;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "album_id")
    private Integer albumId;

    @Column(name = "artist_id")
    private Integer artistId;
}