package com.crawl.chungkhoan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {
    private Long id;
    private String title;
    private String url;
    private String source;
    private String description;
    private String imageUrl;
    private Set<String> tags;
    private LocalDateTime publishedAt;
    private LocalDateTime crawledAt;
}

