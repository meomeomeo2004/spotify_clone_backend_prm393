package com.example.spotify.repository.projection;

public interface TrackSearchRow {
    Long getTrackId();
    String getTitle();
    Integer getDuration();
    String getAudioUrl();
    String getImageUrl();
    Long getPlayCount();
    String getAlbumTitle();
    String getArtists(); // GROUP_CONCAT
    String getGenres();  // GROUP_CONCAT
}
