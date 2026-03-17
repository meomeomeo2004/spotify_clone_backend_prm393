package com.example.spotify.repository;

import com.example.spotify.dto.ArtistDto;
import com.example.spotify.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    @Query("SELECT new com.example.spotify.dto.ArtistDto(a.id, a.imageUrl, a.name, a.bio) " +
            "FROM Artist a " +
            "Join ArtistFollower af on a.id = af.artist.id " +
            "Group by a.id, a.imageUrl, a.name, a.bio " +
            "Order by COUNT(af.artist.id) DESC LIMIT 10")
    List<ArtistDto> findTop10Artist();

    @Query("SELECT new com.example.spotify.dto.ArtistDto(a.id, a.imageUrl, a.name, a.bio) " +
            "FROM Artist a")
    List<ArtistDto> findAllArtist();
}
