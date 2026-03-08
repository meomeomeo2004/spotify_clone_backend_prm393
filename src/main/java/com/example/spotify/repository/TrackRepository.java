package com.example.spotify.repository;

import com.example.spotify.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface TrackRepository extends JpaRepository<Track, Integer> {
}