package com.crawl.chungkhoan.service;

import com.crawl.chungkhoan.model.CrawlSource;
import com.crawl.chungkhoan.model.News;
import com.crawl.chungkhoan.repository.CrawlSourceRepository;
import com.crawl.chungkhoan.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlService {

    private final NewsRepository newsRepository;
    private final CrawlSourceRepository crawlSourceRepository;

    /**
     * Crawl tất cả sources đang enabled
     */
    public void crawlAllSources() {
        List<CrawlSource> sources = crawlSourceRepository.findByEnabled(true);
        log.info("Starting crawl for {} enabled sources", sources.size());
        
        for (CrawlSource source : sources) {
            try {
                crawlSource(source);
            } catch (Exception e) {
                log.error("Error crawling source {}: {}", source.getName(), e.getMessage());
            }
        }
        
        log.info("Crawl completed for all sources");
    }

    /**
     * Crawl một source cụ thể
     */
    public void crawlSource(CrawlSource source) {
        log.info("Crawling source: {} - {}", source.getName(), source.getUrl());
        
        try {
            List<News> newsList = new ArrayList<>();
            
            // Crawl theo loại source
            switch (source.getName().toUpperCase()) {
                case "CAFEF":
                    newsList = crawlCafeF();
                    break;
                case "VIETSTOCK":
                    newsList = crawlVietStock();
                    break;
                case "SSI":
                    newsList = crawlSSI();
                    break;
                default:
                    log.warn("Unknown source: {}", source.getName());
            }
            
            // Lưu vào database
            for (News news : newsList) {
                // Check duplicate by URL
                if (!newsRepository.existsByUrl(news.getUrl())) {
                    newsRepository.save(news);
                    log.debug("Saved news: {}", news.getTitle());
                }
            }
            
            log.info("Crawled {} news items from {}", newsList.size(), source.getName());
            
        } catch (Exception e) {
            log.error("Error crawling {}: {}", source.getName(), e.getMessage(), e);
        }
    }

    /**
     * Crawl CafeF - Tin tức chứng khoán
     */
    private List<News> crawlCafeF() {
        List<News> newsList = new ArrayList<>();

        try {
            // URL mới của CafeF
            String url = "https://cafef.vn/chung-khoan.chn";
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(15000)
                    .followRedirects(true)
                    .get();

            // Thử nhiều selector khác nhau
            Elements articles = doc.select("article, .item-news, .box-category-item, .list-news-subfolder li");

            log.info("CafeF: Found {} articles", articles.size());

            for (Element article : articles.stream().limit(15).toList()) {
                try {
                    // Thử nhiều selector cho title
                    Element titleElement = article.selectFirst("h3 a, h2 a, h4 a, .title a, a[title]");

                    if (titleElement != null && !titleElement.text().isEmpty()) {
                        String title = titleElement.text().trim();
                        String newsUrl = titleElement.absUrl("href");

                        // Skip nếu URL không hợp lệ
                        if (newsUrl.isEmpty() || !newsUrl.startsWith("http")) {
                            continue;
                        }

                        // Description
                        Element descElement = article.selectFirst(".sapo, .description, p");
                        String description = descElement != null ? descElement.text().trim() : "";

                        // Image
                        Element imgElement = article.selectFirst("img");
                        String imageUrl = null;
                        if (imgElement != null) {
                            imageUrl = imgElement.absUrl("src");
                            if (imageUrl.isEmpty()) {
                                imageUrl = imgElement.absUrl("data-src");
                            }
                        }

                        News news = News.builder()
                                .title(title)
                                .url(newsUrl)
                                .urlHash(String.valueOf(newsUrl.hashCode()))
                                .source("CAFEF")
                                .description(description)
                                .imageUrl(imageUrl)
                                .publishedAt(LocalDateTime.now())
                                .crawledAt(LocalDateTime.now())
                                .build();

                        newsList.add(news);
                        log.debug("CafeF: Parsed - {}", title);
                    }
                } catch (Exception e) {
                    log.debug("Error parsing CafeF article: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Error crawling CafeF: {}", e.getMessage());
        }

        return newsList;
    }

    /**
     * Crawl VietStock - Tin tức thị trường
     */
    private List<News> crawlVietStock() {
        List<News> newsList = new ArrayList<>();

        try {
            String url = "https://vietstock.vn/";
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(15000)
                    .followRedirects(true)
                    .get();

            Elements articles = doc.select(".news-item, article, .box-news li, .list-news li");

            log.info("VietStock: Found {} articles", articles.size());

            for (Element article : articles.stream().limit(15).toList()) {
                try {
                    Element titleElement = article.selectFirst("h3 a, h2 a, .title a, a[title]");

                    if (titleElement != null && !titleElement.text().isEmpty()) {
                        String title = titleElement.text().trim();
                        String newsUrl = titleElement.absUrl("href");

                        if (newsUrl.isEmpty() || !newsUrl.startsWith("http")) {
                            continue;
                        }

                        Element descElement = article.selectFirst(".description, .sapo, p");
                        String description = descElement != null ? descElement.text().trim() : "";

                        Element imgElement = article.selectFirst("img");
                        String imageUrl = null;
                        if (imgElement != null) {
                            imageUrl = imgElement.absUrl("src");
                            if (imageUrl.isEmpty()) {
                                imageUrl = imgElement.absUrl("data-src");
                            }
                        }

                        News news = News.builder()
                                .title(title)
                                .url(newsUrl)
                                .urlHash(String.valueOf(newsUrl.hashCode()))
                                .source("VIETSTOCK")
                                .description(description)
                                .imageUrl(imageUrl)
                                .publishedAt(LocalDateTime.now())
                                .crawledAt(LocalDateTime.now())
                                .build();

                        newsList.add(news);
                        log.debug("VietStock: Parsed - {}", title);
                    }
                } catch (Exception e) {
                    log.debug("Error parsing VietStock article: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Error crawling VietStock: {}", e.getMessage());
        }

        return newsList;
    }

    /**
     * Crawl SSI - Tin tức đầu tư
     */
    private List<News> crawlSSI() {
        List<News> newsList = new ArrayList<>();

        try {
            // Thử URL mới của SSI
            String url = "https://www.ssi.com.vn/";
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(15000)
                    .followRedirects(true)
                    .get();

            Elements articles = doc.select(".news-item, article, .box-news li, .list-news li");

            log.info("SSI: Found {} articles", articles.size());

            for (Element article : articles.stream().limit(15).toList()) {
                try {
                    Element titleElement = article.selectFirst("h3 a, h2 a, .title a, a[title]");

                    if (titleElement != null && !titleElement.text().isEmpty()) {
                        String title = titleElement.text().trim();
                        String newsUrl = titleElement.absUrl("href");

                        if (newsUrl.isEmpty() || !newsUrl.startsWith("http")) {
                            continue;
                        }

                        Element descElement = article.selectFirst(".description, .summary, p");
                        String description = descElement != null ? descElement.text().trim() : "";

                        Element imgElement = article.selectFirst("img");
                        String imageUrl = null;
                        if (imgElement != null) {
                            imageUrl = imgElement.absUrl("src");
                            if (imageUrl.isEmpty()) {
                                imageUrl = imgElement.absUrl("data-src");
                            }
                        }

                        News news = News.builder()
                                .title(title)
                                .url(newsUrl)
                                .urlHash(String.valueOf(newsUrl.hashCode()))
                                .source("SSI")
                                .description(description)
                                .imageUrl(imageUrl)
                                .publishedAt(LocalDateTime.now())
                                .crawledAt(LocalDateTime.now())
                                .build();

                        newsList.add(news);
                        log.debug("SSI: Parsed - {}", title);
                    }
                } catch (Exception e) {
                    log.debug("Error parsing SSI article: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Error crawling SSI: {}", e.getMessage());
        }

        return newsList;
    }
}

