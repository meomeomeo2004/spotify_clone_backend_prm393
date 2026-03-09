package com.example.spotify.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "tracks")
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "track_id")
    private Long trackId;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "audio_url", nullable = false, length = 255)
    private String audioUrl;

    @Column(columnDefinition = "TEXT", name = "lyrics", nullable = false, length = 10000)
    private String lyric;

}
