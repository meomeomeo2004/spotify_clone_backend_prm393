package com.example.spotify.service;

import com.example.spotify.dto.NewsArticleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fetches Billboard Music News from the public RSS feed.
 * No external API key required.
 * Results are cached for {@value #CACHE_TTL_MIN} minutes.
 */
@Service
public class NewsService {

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);

    private static final String RSS_URL =
            "https://www.billboard.com/c/music/music-news/feed/";

    private static final int CACHE_TTL_MIN = 30;
    private static final Duration CACHE_TTL = Duration.ofMinutes(CACHE_TTL_MIN);

    // Namespace URIs used in Billboard RSS
    private static final String NS_DC      = "http://purl.org/dc/elements/1.1/";
    private static final String NS_CONTENT = "http://purl.org/rss/1.0/modules/content/";

    // Billboard lazy-loads images via data-lazy-src; fallback to regular src
    private static final Pattern PAT_LAZY_SRC =
            Pattern.compile("data-lazy-src=\"(https://[^\"]+)\"");
    private static final Pattern PAT_SRC =
            Pattern.compile("<img[^>]+\\bsrc=\"(https://[^\"]+)\"");

    private final RestTemplate restTemplate = new RestTemplate();

    private volatile List<NewsArticleDto> cache = null;
    private volatile Instant cacheExpiry = Instant.MIN;

    // ─────────────────────────────────────────────────────────────────────────

    public List<NewsArticleDto> getMusicNews() {
        if (cache != null && Instant.now().isBefore(cacheExpiry)) {
            log.debug("Serving {} cached news articles", cache.size());
            return cache;
        }
        synchronized (this) {
            if (cache != null && Instant.now().isBefore(cacheExpiry)) return cache;

            List<NewsArticleDto> fresh = fetchFromRss();

            if (fresh.isEmpty() && cache != null && !cache.isEmpty()) {
                log.warn("RSS fetch returned empty; extending stale cache");
                cacheExpiry = Instant.now().plus(CACHE_TTL);
                return cache;
            }

            cache = fresh;
            cacheExpiry = Instant.now().plus(CACHE_TTL);
            log.info("News cache refreshed: {} articles", fresh.size());
            return fresh;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    private List<NewsArticleDto> fetchFromRss() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (compatible; SpringBot/1.0)");
            headers.set("Accept", "application/rss+xml,application/xml,text/xml,*/*");

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    RSS_URL, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);

            if (response.getBody() == null) {
                log.warn("RSS response body is null");
                return Collections.emptyList();
            }

            String xml = new String(response.getBody(), StandardCharsets.UTF_8);
            return parseRss(xml);

        } catch (Exception e) {
            log.error("Failed to fetch RSS feed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<NewsArticleDto> parseRss(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            // Security hardening: disable external entity processing
            factory.setFeature(
                    "http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature(
                    "http://xml.org/sax/features/external-parameter-entities", false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(null); // suppress non-fatal parse warnings

            Document doc = builder.parse(new InputSource(new StringReader(xml)));
            NodeList items = doc.getElementsByTagName("item");

            List<NewsArticleDto> result = new ArrayList<>();
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                NewsArticleDto dto = mapItem(item);
                if (dto != null) result.add(dto);
            }
            return result;

        } catch (Exception e) {
            log.error("Failed to parse RSS XML: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private NewsArticleDto mapItem(Element item) {
        String title       = stripHtml(text(item, "title"));
        String link        = text(item, "link").trim();

        if (title.isEmpty() || link.isEmpty() || !link.startsWith("http")) return null;

        String description = stripHtml(text(item, "description"));
        String pubDateStr  = text(item, "pubDate");
        String author      = textNs(item, NS_DC, "creator");
        if (author.isBlank()) author = "Billboard";

        // Extract the first article image from the full HTML content
        String contentHtml = textNs(item, NS_CONTENT, "encoded");
        String imageUrl    = extractFirstImage(contentHtml);

        return new NewsArticleDto(
                title,
                description.isBlank() ? null : description,
                link,
                imageUrl,
                toIso(pubDateStr),
                "Billboard",
                author.isBlank() ? null : author
        );
    }

    // ── XML helpers ───────────────────────────────────────────────────────────

    /** Text content of the first element with the given tag in any namespace. */
    private String text(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) return "";
        return nodes.item(0).getTextContent().trim();
    }

    /** Text content of the first element in a specific namespace. */
    private String textNs(Element parent, String ns, String localName) {
        NodeList nodes = parent.getElementsByTagNameNS(ns, localName);
        if (nodes.getLength() == 0) return "";
        return nodes.item(0).getTextContent().trim();
    }

    // ── Image extraction ──────────────────────────────────────────────────────

    /**
     * Extracts the first image URL from HTML content.
     * Billboard lazy-loads images via {@code data-lazy-src}; regular {@code src}
     * is used as fallback.
     */
    private String extractFirstImage(String html) {
        if (html == null || html.isEmpty()) return null;

        Matcher m = PAT_LAZY_SRC.matcher(html);
        if (m.find()) {
            return cleanUrl(m.group(1));
        }
        m = PAT_SRC.matcher(html);
        if (m.find()) {
            return cleanUrl(m.group(1));
        }
        return null;
    }

    /** Decode HTML entities in a URL string (e.g. &#038; → &). */
    private String cleanUrl(String url) {
        return url.replace("&#038;", "&").replace("&amp;", "&");
    }

    // ── Date conversion ───────────────────────────────────────────────────────

    /**
     * Converts RFC-822 pubDate ("Sun, 22 Mar 2026 06:40:57 +0000")
     * to ISO-8601 ("2026-03-22T06:40:57Z") for easy parsing in Flutter/Dart.
     */
    private String toIso(String rfcDate) {
        if (rfcDate == null || rfcDate.isBlank()) return "";
        try {
            ZonedDateTime zdt = ZonedDateTime.parse(rfcDate,
                    DateTimeFormatter.RFC_1123_DATE_TIME);
            return zdt.withZoneSameInstant(ZoneOffset.UTC)
                      .format(DateTimeFormatter.ISO_INSTANT);
        } catch (Exception e) {
            return rfcDate; // return raw if parse fails
        }
    }

    // ── HTML stripping ────────────────────────────────────────────────────────

    private String stripHtml(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("<[^>]+>", " ")
                  .replaceAll("\\s+", " ")
                  .trim();
    }
}
