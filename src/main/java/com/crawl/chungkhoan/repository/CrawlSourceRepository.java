package com.crawl.chungkhoan.repository;

import com.crawl.chungkhoan.model.CrawlSource;
import com.crawl.chungkhoan.storage.InMemoryStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CrawlSourceRepository {

    private final InMemoryStorage storage;

    public CrawlSource save(CrawlSource source) {
        return storage.saveSource(source);
    }

    public Optional<CrawlSource> findById(Long id) {
        return storage.findSourceById(id);
    }

    public List<CrawlSource> findByEnabled(Boolean enabled) {
        return storage.findAllSources().stream()
                .filter(s -> enabled.equals(s.isEnabled()))
                .collect(Collectors.toList());
    }

    public List<CrawlSource> findAll() {
        return storage.findAllSources();
    }

    public void deleteById(Long id) {
        storage.deleteSource(id);
    }
}

