package com.crawl.chungkhoan.repository;

import com.crawl.chungkhoan.model.News;
import com.crawl.chungkhoan.storage.InMemoryStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NewsRepository {

    private final InMemoryStorage storage;

    public News save(News news) {
        return storage.saveNews(news);
    }

    public Optional<News> findById(Long id) {
        return storage.findNewsById(id);
    }

    public Optional<News> findByUrlHash(String urlHash) {
        return storage.findNewsByUrlHash(urlHash);
    }

    public List<News> findAll() {
        return storage.findAllNews();
    }

    public List<News> findBySource(String source) {
        return storage.findNewsBySource(source);
    }

    public List<News> findRecentNews(LocalDateTime since) {
        return storage.findAllNews().stream()
                .filter(n -> n.getPublishedAt().isAfter(since))
                .sorted(Comparator.comparing(News::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<News> searchByKeyword(String keyword) {
        return storage.findAllNews().stream()
                .filter(n -> n.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .sorted(Comparator.comparing(News::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        storage.deleteNews(id);
    }

    public boolean existsByUrl(String url) {
        return storage.findAllNews().stream()
                .anyMatch(n -> n.getUrl().equals(url));
    }
}

