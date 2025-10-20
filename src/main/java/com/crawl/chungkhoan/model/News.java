package com.crawl.chungkhoan.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class News {

    private Long id;
    private String title;
    private String url;
    private String urlHash;
    private String source;
    private String description;
    private String content;
    private String imageUrl;

    @Builder.Default
    private Set<String> tags = new HashSet<>();

    private LocalDateTime publishedAt;
    private LocalDateTime crawledAt;
}

