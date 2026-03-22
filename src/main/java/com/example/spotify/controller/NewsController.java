package com.example.spotify.controller;

import com.example.spotify.dto.NewsArticleDto;
import com.example.spotify.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * GET /api/news
 * Returns a list of current music news articles sourced from NewsAPI.org.
 * The API key is stored in application.properties and never exposed to clients.
 */
@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<List<NewsArticleDto>> getNews() {
        try {
            List<NewsArticleDto> articles = newsService.getMusicNews();
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            // Return empty list rather than 500 so Flutter can show "no news" state
            return ResponseEntity.ok(Collections.emptyList());
        }
    }
}
