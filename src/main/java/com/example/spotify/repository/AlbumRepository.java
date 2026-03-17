package com.example.spotify.repository;

import com.example.spotify.dto.AlbumDto;
import com.example.spotify.entity.Album;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NullMarked
public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query("SELECT new com.example.spotify.dto.AlbumDto(a.albumId, a.imageUrl, a.title, ar.name) " +
            "FROM Album a " +
            "join AlbumLike al on a.albumId = al.album.albumId " +
            "join AlbumArtist aa on a.albumId = aa.album.albumId " +
            "join Artist ar on aa.artist.id = ar.id " +
            "Group by a.albumId, ar.name, a.title, a.imageUrl " +
            "Order by COUNT(al.album.albumId) Desc LIMIT 10")
    List<AlbumDto> findTop10Album();


    @Query("SELECT aa.album FROM AlbumArtist aa WHERE aa.artist.id = :artistId")
    List<Album> findAlbumsByArtistId(@Param("artistId") Long artistId);
}

