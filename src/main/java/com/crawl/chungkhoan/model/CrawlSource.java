package com.crawl.chungkhoan.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlSource {

    private Long id;
    private String name;
    private String url;
    private SourceType type;
    private String cssSelector;

    @Builder.Default
    private Integer crawlInterval = 300; // seconds

    @Builder.Default
    private boolean enabled = true;

    private LocalDateTime lastCrawledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum SourceType {
        NEWS, RSS, API
    }
}

