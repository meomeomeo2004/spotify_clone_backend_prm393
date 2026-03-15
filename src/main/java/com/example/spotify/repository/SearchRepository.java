package com.example.spotify.repository;
import com.example.spotify.entity.Track;
import com.example.spotify.repository.projection.TrackSearchRow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchRepository extends Repository<Track, Long> {

    @Query(value = """
    SELECT
        t.track_id AS trackId,
        t.title AS title,
        t.duration AS duration,
        t.audio_url AS audioUrl,
        t.image_url AS imageUrl,
        t.play_count AS playCount,
        al.title AS albumTitle,
        GROUP_CONCAT(DISTINCT ar.name SEPARATOR '||') AS artists,
        GROUP_CONCAT(DISTINCT g.name SEPARATOR '||') AS genres
    FROM tracks t
    LEFT JOIN albums al ON t.album_id = al.album_id
    LEFT JOIN track_artists ta ON t.track_id = ta.track_id
    LEFT JOIN artists ar ON ta.artist_id = ar.artist_id
    LEFT JOIN track_genres tg ON t.track_id = tg.track_id
    LEFT JOIN genres g ON tg.genre_id = g.genre_id
    WHERE
        (:q = '' OR LOWER(t.title) LIKE CONCAT('%', LOWER(:q), '%'))
        AND (:genreFilterOff = true OR tg.genre_id IN (:genreIds))
    GROUP BY t.track_id, t.title, t.duration, t.audio_url, t.image_url, t.play_count, al.title
    ORDER BY t.play_count DESC, t.created_at DESC
    """, nativeQuery = true)
    List<TrackSearchRow> searchTracks(
            @Param("q") String q,
            @Param("genreFilterOff") boolean genreFilterOff,
            @Param("genreIds") List<Integer> genreIds,
            Pageable pageable
    );

    @Query(value = """
        SELECT COUNT(DISTINCT t.track_id)
        FROM tracks t
        LEFT JOIN albums al ON t.album_id = al.album_id
        LEFT JOIN track_artists ta ON t.track_id = ta.track_id
        LEFT JOIN artists ar ON ta.artist_id = ar.artist_id
        LEFT JOIN track_genres tg ON t.track_id = tg.track_id
        WHERE
            (:q = '' OR LOWER(t.title) LIKE CONCAT('%', LOWER(:q), '%'))
            AND
            (:genreFilterOff = true OR tg.genre_id IN (:genreIds))
        """,
            nativeQuery = true)
    long countTracks(
            @Param("q") String q,
            @Param("genreFilterOff") boolean genreFilterOff,
            @Param("genreIds") List<Integer> genreIds
    );

    @Query(value = """
    SELECT
        t.track_id AS trackId,
        t.title AS title,
        NULL AS duration,
        t.audio_url AS audioUrl,
        t.image_url AS imageUrl,
        t.play_count AS playCount,
        al.title AS albumTitle,
        GROUP_CONCAT(DISTINCT ar.name SEPARATOR '||') AS artists,
        '' AS genres
    FROM tracks t
    LEFT JOIN albums al ON t.album_id = al.album_id
    LEFT JOIN track_artists ta ON t.track_id = ta.track_id
    LEFT JOIN artists ar ON ta.artist_id = ar.artist_id
    WHERE
        (:q = '' OR LOWER(t.title) LIKE CONCAT('%', LOWER(:q), '%'))
    GROUP BY t.track_id, t.title, t.audio_url, t.image_url, t.play_count, al.title
    ORDER BY t.play_count DESC, t.created_at DESC
    """, nativeQuery = true)
    List<TrackSearchRow> searchTracksFallback(
            @Param("q") String q,
            Pageable pageable
    );

    @Query(value = """
        SELECT COUNT(DISTINCT t.track_id)
        FROM tracks t
        LEFT JOIN albums al ON t.album_id = al.album_id
        LEFT JOIN track_artists ta ON t.track_id = ta.track_id
        LEFT JOIN artists ar ON ta.artist_id = ar.artist_id
        WHERE
            (:q = '' OR LOWER(t.title) LIKE CONCAT('%', LOWER(:q), '%'))
        """,
            nativeQuery = true)
    long countTracksFallback(
            @Param("q") String q
    );


    @Query(value = """
    SELECT t.title
    FROM tracks t
    WHERE LOWER(t.title) LIKE CONCAT(LOWER(:q), '%')
    GROUP BY t.title
    ORDER BY MAX(t.play_count) DESC
    LIMIT 8
    """, nativeQuery = true)
    List<String> suggestTrackTitles(@Param("q") String q);

    @Query(value = """
    SELECT DISTINCT a.name
    FROM artists a
    WHERE LOWER(a.name) LIKE CONCAT(LOWER(:q), '%')
    ORDER BY a.name ASC
    LIMIT 8
    """, nativeQuery = true)
    List<String> suggestArtists(@Param("q") String q);

    @Query(value = """
    SELECT DISTINCT g.name
    FROM genres g
    WHERE LOWER(g.name) LIKE CONCAT(LOWER(:q), '%')
    ORDER BY g.name ASC
    LIMIT 8
    """, nativeQuery = true)
    List<String> suggestGenres(@Param("q") String q);
}
