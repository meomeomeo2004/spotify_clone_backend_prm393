package com.example.spotify.controller;

import com.example.spotify.dto.GenreDto;
import com.example.spotify.dto.SearchSuggestionsDto;
import com.example.spotify.dto.TrackSearchResponseDto;
import com.example.spotify.service.SearchService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/genres")
    public List<GenreDto> getGenres() {
        return searchService.getGenres();
    }

    @GetMapping("/search/suggestions")
    public SearchSuggestionsDto getSuggestions(@RequestParam(defaultValue = "") String q) {
        return searchService.getSuggestions(q);
    }

    @GetMapping("/search/tracks")
    public TrackSearchResponseDto searchTracks(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) String genreIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<Integer> parsedGenreIds = parseGenreIds(genreIds);
        return searchService.searchTracks(q, parsedGenreIds, page, size);
    }

    private List<Integer> parseGenreIds(String genreIds) {
        List<Integer> result = new ArrayList<>();
        if (genreIds == null || genreIds.isBlank()) return result;

        String[] parts = genreIds.split(",");
        for (String p : parts) {
            try {
                result.add(Integer.parseInt(p.trim()));
            } catch (NumberFormatException ignored) {}
        }
        return result;
    }
}