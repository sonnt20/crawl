package com.crawl.chungkhoan.service;

import com.crawl.chungkhoan.model.CrawlSource;
import com.crawl.chungkhoan.model.News;
import com.crawl.chungkhoan.repository.CrawlSourceRepository;
import com.crawl.chungkhoan.repository.NewsRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeleniumCrawlService {

    private final NewsRepository newsRepository;
    private final CrawlSourceRepository crawlSourceRepository;

    /**
     * Crawl tất cả sources đang enabled
     */
    public void crawlAllSources() {
        List<CrawlSource> sources = crawlSourceRepository.findByEnabled(true);
        log.info("Starting Selenium crawl for {} enabled sources", sources.size());
        
        for (CrawlSource source : sources) {
            try {
                crawlSource(source);
            } catch (Exception e) {
                log.error("Error crawling source {}: {}", source.getName(), e.getMessage(), e);
            }
        }
        
        log.info("Selenium crawl completed for all sources");
    }

    /**
     * Crawl một source cụ thể
     */
    public void crawlSource(CrawlSource source) {
        log.info("Selenium crawling source: {} - {}", source.getName(), source.getUrl());
        
        WebDriver driver = null;
        try {
            // Setup ChromeDriver
            driver = setupChromeDriver();
            
            List<News> newsList = new ArrayList<>();
            
            // Crawl theo loại source
            switch (source.getName().toUpperCase()) {
                case "CAFEF":
                    newsList = crawlCafeF(driver);
                    break;
                case "VIETSTOCK":
                    newsList = crawlVietStock(driver);
                    break;
                case "SSI":
                    newsList = crawlSSI(driver);
                    break;
                default:
                    log.warn("Unknown source: {}", source.getName());
            }
            
            // Lưu vào database
            int savedCount = 0;
            for (News news : newsList) {
                // Check duplicate by URL
                if (!newsRepository.existsByUrl(news.getUrl())) {
                    newsRepository.save(news);
                    savedCount++;
                    log.debug("Saved news: {}", news.getTitle());
                }
            }
            
            log.info("Crawled {} news items from {} ({} new items saved)", 
                    newsList.size(), source.getName(), savedCount);
            
        } catch (Exception e) {
            log.error("Error crawling source {}: {}", source.getName(), e.getMessage(), e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    /**
     * Setup ChromeDriver với options
     */
    private WebDriver setupChromeDriver() {
        log.info("Setting up ChromeDriver...");
        
        // WebDriverManager tự động download và setup ChromeDriver
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Chạy không hiển thị browser
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        
        log.info("ChromeDriver setup completed");
        return driver;
    }

    /**
     * Crawl CafeF - Tin tức chứng khoán
     */
    private List<News> crawlCafeF(WebDriver driver) {
        List<News> newsList = new ArrayList<>();
        
        try {
            String url = "https://cafef.vn/chung-khoan.chn";
            log.info("CafeF: Loading page {}", url);
            driver.get(url);
            
            // Đợi page load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            
            // Thử nhiều selector khác nhau
            List<WebElement> articles = driver.findElements(By.cssSelector("article, .item-news, .box-category-item, .list-news-subfolder li, .tlitem"));
            
            log.info("CafeF: Found {} articles", articles.size());
            
            int count = 0;
            for (WebElement article : articles) {
                if (count >= 15) break;
                
                try {
                    // Tìm title link
                    WebElement titleElement = null;
                    try {
                        titleElement = article.findElement(By.cssSelector("h3 a, h2 a, h4 a, .title a, a[title]"));
                    } catch (Exception e) {
                        continue;
                    }
                    
                    if (titleElement != null) {
                        String title = titleElement.getText().trim();
                        String newsUrl = titleElement.getAttribute("href");
                        
                        if (title.isEmpty() || newsUrl == null || newsUrl.isEmpty()) {
                            continue;
                        }
                        
                        // Description
                        String description = "";
                        try {
                            WebElement descElement = article.findElement(By.cssSelector(".sapo, .description, p"));
                            description = descElement.getText().trim();
                        } catch (Exception e) {
                            // Ignore
                        }
                        
                        // Image
                        String imageUrl = null;
                        try {
                            WebElement imgElement = article.findElement(By.cssSelector("img"));
                            imageUrl = imgElement.getAttribute("src");
                            if (imageUrl == null || imageUrl.isEmpty()) {
                                imageUrl = imgElement.getAttribute("data-src");
                            }
                        } catch (Exception e) {
                            // Ignore
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
                        count++;
                        log.debug("CafeF: Parsed - {}", title);
                    }
                } catch (Exception e) {
                    log.debug("Error parsing CafeF article: {}", e.getMessage());
                }
            }
            
        } catch (Exception e) {
            log.error("Error crawling CafeF: {}", e.getMessage(), e);
        }
        
        return newsList;
    }

    /**
     * Crawl VietStock - Tin tức thị trường
     */
    private List<News> crawlVietStock(WebDriver driver) {
        List<News> newsList = new ArrayList<>();
        
        try {
            String url = "https://vietstock.vn/";
            log.info("VietStock: Loading page {}", url);
            driver.get(url);
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            
            List<WebElement> articles = driver.findElements(By.cssSelector(".news-item, article, .box-news li, .list-news li"));
            
            log.info("VietStock: Found {} articles", articles.size());
            
            int count = 0;
            for (WebElement article : articles) {
                if (count >= 15) break;
                
                try {
                    WebElement titleElement = null;
                    try {
                        titleElement = article.findElement(By.cssSelector("h3 a, h2 a, .title a, a[title]"));
                    } catch (Exception e) {
                        continue;
                    }
                    
                    if (titleElement != null) {
                        String title = titleElement.getText().trim();
                        String newsUrl = titleElement.getAttribute("href");
                        
                        if (title.isEmpty() || newsUrl == null || newsUrl.isEmpty()) {
                            continue;
                        }
                        
                        String description = "";
                        try {
                            WebElement descElement = article.findElement(By.cssSelector(".description, .sapo, p"));
                            description = descElement.getText().trim();
                        } catch (Exception e) {
                            // Ignore
                        }
                        
                        String imageUrl = null;
                        try {
                            WebElement imgElement = article.findElement(By.cssSelector("img"));
                            imageUrl = imgElement.getAttribute("src");
                            if (imageUrl == null || imageUrl.isEmpty()) {
                                imageUrl = imgElement.getAttribute("data-src");
                            }
                        } catch (Exception e) {
                            // Ignore
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
                        count++;
                        log.debug("VietStock: Parsed - {}", title);
                    }
                } catch (Exception e) {
                    log.debug("Error parsing VietStock article: {}", e.getMessage());
                }
            }
            
        } catch (Exception e) {
            log.error("Error crawling VietStock: {}", e.getMessage(), e);
        }
        
        return newsList;
    }

    /**
     * Crawl SSI - Tin tức đầu tư
     */
    private List<News> crawlSSI(WebDriver driver) {
        List<News> newsList = new ArrayList<>();
        
        try {
            String url = "https://www.ssi.com.vn/";
            log.info("SSI: Loading page {}", url);
            driver.get(url);
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            
            List<WebElement> articles = driver.findElements(By.cssSelector(".news-item, article, .box-news li, .list-news li"));
            
            log.info("SSI: Found {} articles", articles.size());
            
            // SSI implementation tương tự VietStock
            // ... (code tương tự)
            
        } catch (Exception e) {
            log.error("Error crawling SSI: {}", e.getMessage(), e);
        }
        
        return newsList;
    }
}

