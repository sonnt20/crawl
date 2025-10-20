package com.crawl.chungkhoan.controller;

import com.crawl.chungkhoan.dto.NewsResponse;
import com.crawl.chungkhoan.dto.PageResponse;
import com.crawl.chungkhoan.model.News;
import com.crawl.chungkhoan.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsRepository newsRepository;

    @GetMapping
    public ResponseEntity<PageResponse<NewsResponse>> getAllNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<News> allNews = newsRepository.findAll();

        int totalElements = allNews.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<NewsResponse> content = allNews.stream()
                .skip((long) page * size)
                .limit(size)
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        PageResponse<NewsResponse> response = new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<NewsResponse>> searchNews(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<News> allNews = newsRepository.searchByKeyword(keyword);
        List<NewsResponse> response = allNews.stream()
                .skip((long) page * size)
                .limit(size)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/source/{source}")
    public ResponseEntity<List<NewsResponse>> getNewsBySource(
            @PathVariable String source,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<News> allNews = newsRepository.findBySource(source);
        List<NewsResponse> response = allNews.stream()
                .skip((long) page * size)
                .limit(size)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable Long id) {
        return newsRepository.findById(id)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private NewsResponse convertToResponse(News news) {
        return NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .url(news.getUrl())
                .source(news.getSource())
                .description(news.getDescription())
                .imageUrl(news.getImageUrl())
                .tags(news.getTags())
                .publishedAt(news.getPublishedAt())
                .crawledAt(news.getCrawledAt())
                .build();
    }
}

