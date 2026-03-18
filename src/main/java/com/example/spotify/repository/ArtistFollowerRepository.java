package com.example.spotify.repository;

import com.example.spotify.entity.ArtistFollower;
import com.example.spotify.entity.ArtistFollowerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistFollowerRepository extends JpaRepository<ArtistFollower, ArtistFollowerId> {
}