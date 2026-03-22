package com.example.spotify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Article data returned by GET /api/news.
 * All fields are nullable-safe (empty string instead of null for required ones).
 */
@Getter
@Setter
@AllArgsConstructor
public class NewsArticleDto {
    /** Headline of the article. */
    private String title;
    /** Short description / lede. May be null. */
    private String description;
    /** Canonical URL of the article on the source website. */
    private String articleUrl;
    /** Thumbnail image URL. May be null if source did not provide one. */
    private String imageUrl;
    /** ISO-8601 publication datetime, e.g. "2026-03-22T10:00:00Z". */
    private String publishedAt;
    /** Human-readable source name, e.g. "Billboard". */
    private String sourceName;
    /** Author name. May be null. */
    private String author;
}
