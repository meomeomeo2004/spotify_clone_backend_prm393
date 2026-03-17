package com.example.spotify.repository;

import com.example.spotify.dto.AlbumDto;

import com.example.spotify.entity.Album;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NullMarked
public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query("SELECT new com.example.spotify.dto.AlbumDto(a.id, a.imageUrl, a.title, ar.name) " +
            "FROM Album a " +
            "join AlbumLike al on a.id = al.album.id " +
            "join AlbumArtist aa on a.id = aa.album.id " +
            "join Artist ar on aa.artist.id = ar.id " +
            "Group by a.id, ar.name, a.title, a.imageUrl " +
            "Order by COUNT(al.album.id) Desc LIMIT 10")
    List<AlbumDto> findTop10Album();
}
