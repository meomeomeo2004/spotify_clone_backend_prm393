package com.example.spotify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.spotify.entity.Track;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

}
