package com.example.spotify.service;

import java.util.*;
import java.util.stream.Collectors;

import com.example.spotify.dto.GenreDto;
import com.example.spotify.dto.SearchSuggestionsDto;
import com.example.spotify.dto.TrackSearchItemDto;
import com.example.spotify.dto.TrackSearchResponseDto;
import com.example.spotify.entity.Genre;
import com.example.spotify.repository.GenreRepository;
import com.example.spotify.repository.SearchRepository;
import com.example.spotify.repository.projection.TrackSearchRow;
import org.springframework.data.domain.PageRequest;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final SearchRepository searchRepository;
    private final GenreRepository genreRepository;

    public SearchService(SearchRepository searchRepository, GenreRepository genreRepository) {
        this.searchRepository = searchRepository;
        this.genreRepository = genreRepository;
    }

    public List<GenreDto> getGenres() {
        return genreRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Genre::getName))
                .map(g -> new GenreDto(g.getGenreId(), g.getName()))
                .collect(Collectors.toList());
    }

    public SearchSuggestionsDto getSuggestions(String q) {
        String keyword = q == null ? "" : q.trim();
        if (keyword.isEmpty()) {
            return new SearchSuggestionsDto(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        }

        List<String> tracks = searchRepository.suggestTrackTitles(keyword);
        List<String> artists = searchRepository.suggestArtists(keyword);
        List<String> genres = searchRepository.suggestGenres(keyword);

        return new SearchSuggestionsDto(tracks, artists, genres);
    }

    public TrackSearchResponseDto searchTracks(String q, List<Integer> genreIds, int page, int size) {
        String keyword = q == null ? "" : q.trim();
        List<Integer> safeGenreIds = (genreIds == null) ? Collections.emptyList() : genreIds;
        boolean genreFilterOff = safeGenreIds.isEmpty();

        // Tránh IN () lỗi SQL khi empty
        List<Integer> queryGenreIds = genreFilterOff ? List.of(-1) : safeGenreIds;

        PageRequest pageable = PageRequest.of(page, Math.min(size, 50));
        List<TrackSearchRow> rows;
        long total;
        try {
            rows = searchRepository.searchTracks(keyword, genreFilterOff, queryGenreIds, pageable);
            total = searchRepository.countTracks(keyword, genreFilterOff, queryGenreIds);
        } catch (InvalidDataAccessResourceUsageException ex) {
            // Fallback when DB schema is missing genre mapping tables or duration column.
            rows = searchRepository.searchTracksFallback(keyword, pageable);
            total = searchRepository.countTracksFallback(keyword);
        }

        List<TrackSearchItemDto> items = rows.stream().map(r -> {
            TrackSearchItemDto dto = new TrackSearchItemDto();
            dto.setTrackId(r.getTrackId());
            dto.setTitle(r.getTitle());
            dto.setDuration(r.getDuration());
            dto.setAudioUrl(r.getAudioUrl());
            dto.setImageUrl(r.getImageUrl());
            dto.setPlayCount(r.getPlayCount());
            dto.setAlbumTitle(r.getAlbumTitle());
            dto.setArtists(splitConcat(r.getArtists()));
            dto.setGenres(splitConcat(r.getGenres()));
            return dto;
        }).collect(Collectors.toList());

        return new TrackSearchResponseDto(items, page, size, total);
    }

    private List<String> splitConcat(String value) {
        if (value == null || value.isBlank()) return Collections.emptyList();
        return Arrays.stream(value.split("\\|\\|"))
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }
}
