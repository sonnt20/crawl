package com.crawl.chungkhoan.storage;

import com.crawl.chungkhoan.model.Alert;
import com.crawl.chungkhoan.model.CrawlSource;
import com.crawl.chungkhoan.model.News;
import com.crawl.chungkhoan.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory storage for temporary data storage without database
 */
@Slf4j
@Component
public class InMemoryStorage {

    // Auto-increment IDs
    private final AtomicLong userIdCounter = new AtomicLong(1);
    private final AtomicLong newsIdCounter = new AtomicLong(1);
    private final AtomicLong alertIdCounter = new AtomicLong(1);
    private final AtomicLong sourceIdCounter = new AtomicLong(1);

    // Storage maps
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();
    private final Map<Long, News> news = new ConcurrentHashMap<>();
    private final Map<String, News> newsByUrlHash = new ConcurrentHashMap<>();
    private final Map<Long, Alert> alerts = new ConcurrentHashMap<>();
    private final Map<Long, CrawlSource> sources = new ConcurrentHashMap<>();

    public InMemoryStorage() {
        log.info("InMemoryStorage initialized - using in-memory data storage");
        initializeDefaultData();
    }

    private void initializeDefaultData() {
        // Create default admin user with BCrypt encoded password
        // Password: admin123
        // BCrypt hash generated with strength 10
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("admin123");

        User admin = User.builder()
                .id(userIdCounter.getAndIncrement())
                .email("admin@crawl.com")
                .password(encodedPassword)
                .fullName("Administrator")
                .role(User.Role.ADMIN)
                .subscriptionTier(User.SubscriptionTier.PREMIUM)
                .apiKey(UUID.randomUUID().toString())
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();
        saveUser(admin);
        log.info("Default admin user created: admin@crawl.com / ***");

        // Create default crawl sources
        createDefaultSources();

        // Don't create mock news - wait for real crawl data
        log.info("No mock news created - waiting for real crawl data");
    }

    private void createDefaultSources() {
        List<CrawlSource> defaultSources = Arrays.asList(
                CrawlSource.builder()
                        .id(sourceIdCounter.getAndIncrement())
                        .name("CafeF")
                        .url("https://cafef.vn")
                        .type(CrawlSource.SourceType.NEWS)
                        .enabled(true)
                        .crawlInterval(300)
                        .build(),
                CrawlSource.builder()
                        .id(sourceIdCounter.getAndIncrement())
                        .name("VietStock")
                        .url("https://vietstock.vn")
                        .type(CrawlSource.SourceType.NEWS)
                        .enabled(true)
                        .crawlInterval(300)
                        .build(),
                CrawlSource.builder()
                        .id(sourceIdCounter.getAndIncrement())
                        .name("SSI")
                        .url("https://www.ssi.com.vn")
                        .type(CrawlSource.SourceType.NEWS)
                        .enabled(true)
                        .crawlInterval(300)
                        .build()
        );

        defaultSources.forEach(this::saveSource);
        log.info("Created {} default crawl sources", defaultSources.size());
    }

    private void createMockNews() {
        String[] sources = {"CAFEF", "VIETSTOCK", "SSI"};
        String[] titles = {
            "VN-Index tăng điểm mạnh trong phiên đầu tuần",
            "Dòng tiền đổ mạnh vào cổ phiếu ngân hàng",
            "Thị trường chứng khoán Việt Nam hấp dẫn nhà đầu tư ngoại",
            "Cổ phiếu công nghệ bứt phá trong phiên chiều",
            "Nhà đầu tư F mua ròng 500 tỷ đồng",
            "Khối ngoại tiếp tục mua ròng cổ phiếu bluechip",
            "Cổ phiếu bất động sản hồi phục mạnh mẽ",
            "Thị trường chứng khoán đón nhận tín hiệu tích cực",
            "Dòng tiền chảy vào nhóm cổ phiếu vốn hóa lớn",
            "Cổ phiếu thép tăng trần hàng loạt",
            "Nhóm cổ phiếu dầu khí bứt phá ấn tượng",
            "VN-Index chinh phục mốc 1,300 điểm",
            "Thanh khoản thị trường đạt mức cao kỷ lục",
            "Cổ phiếu ngân hàng dẫn dắt thị trường",
            "Nhà đầu tư nước ngoài tích cực gom hàng"
        };

        for (int i = 0; i < 15; i++) {
            LocalDateTime publishedAt = LocalDateTime.now().minusHours(i * 2);

            News news = News.builder()
                    .id(newsIdCounter.getAndIncrement())
                    .title(titles[i])
                    .description("Phân tích chi tiết về diễn biến thị trường chứng khoán Việt Nam. " +
                            "Các chuyên gia nhận định xu hướng và cơ hội đầu tư trong thời gian tới.")
                    .url("https://example.com/news/" + i)
                    .source(sources[i % 3])
                    .publishedAt(publishedAt)
                    .crawledAt(LocalDateTime.now())
                    .imageUrl("https://picsum.photos/400/300?random=" + i)
                    .build();

            saveNews(news);
        }

        log.info("Created 15 mock news items");
    }

    // User operations
    public User saveUser(User user) {
        if (user.getId() == null) {
            user.setId(userIdCounter.getAndIncrement());
        }
        users.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user);
        return user;
    }

    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> findUserByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }

    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void deleteUser(Long id) {
        User user = users.remove(id);
        if (user != null) {
            usersByEmail.remove(user.getEmail());
        }
    }

    // News operations
    public News saveNews(News newsItem) {
        if (newsItem.getId() == null) {
            newsItem.setId(newsIdCounter.getAndIncrement());
        }
        news.put(newsItem.getId(), newsItem);
        if (newsItem.getUrlHash() != null) {
            newsByUrlHash.put(newsItem.getUrlHash(), newsItem);
        }
        return newsItem;
    }

    public Optional<News> findNewsById(Long id) {
        return Optional.ofNullable(news.get(id));
    }

    public Optional<News> findNewsByUrlHash(String urlHash) {
        return Optional.ofNullable(newsByUrlHash.get(urlHash));
    }

    public List<News> findAllNews() {
        return news.values().stream()
                .sorted(Comparator.comparing(News::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<News> findNewsBySource(String source) {
        return news.values().stream()
                .filter(n -> source.equals(n.getSource()))
                .sorted(Comparator.comparing(News::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<News> findNewsByDateRange(LocalDateTime start, LocalDateTime end) {
        return news.values().stream()
                .filter(n -> n.getPublishedAt().isAfter(start) && n.getPublishedAt().isBefore(end))
                .sorted(Comparator.comparing(News::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }

    public void deleteNews(Long id) {
        News newsItem = news.remove(id);
        if (newsItem != null && newsItem.getUrlHash() != null) {
            newsByUrlHash.remove(newsItem.getUrlHash());
        }
    }

    // Alert operations
    public Alert saveAlert(Alert alert) {
        if (alert.getId() == null) {
            alert.setId(alertIdCounter.getAndIncrement());
        }
        alerts.put(alert.getId(), alert);
        return alert;
    }

    public Optional<Alert> findAlertById(Long id) {
        return Optional.ofNullable(alerts.get(id));
    }

    public List<Alert> findAlertsByUserId(Long userId) {
        return alerts.values().stream()
                .filter(a -> userId.equals(a.getUserId()))
                .collect(Collectors.toList());
    }

    public List<Alert> findActiveAlerts() {
        return alerts.values().stream()
                .filter(Alert::isEnabled)
                .collect(Collectors.toList());
    }

    public void deleteAlert(Long id) {
        alerts.remove(id);
    }

    // CrawlSource operations
    public CrawlSource saveSource(CrawlSource source) {
        if (source.getId() == null) {
            source.setId(sourceIdCounter.getAndIncrement());
        }
        sources.put(source.getId(), source);
        return source;
    }

    public Optional<CrawlSource> findSourceById(Long id) {
        return Optional.ofNullable(sources.get(id));
    }

    public List<CrawlSource> findAllSources() {
        return new ArrayList<>(sources.values());
    }

    public List<CrawlSource> findEnabledSources() {
        return sources.values().stream()
                .filter(CrawlSource::isEnabled)
                .collect(Collectors.toList());
    }

    public void deleteSource(Long id) {
        sources.remove(id);
    }

    // Statistics
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", users.size());
        stats.put("totalNews", news.size());
        stats.put("totalAlerts", alerts.size());
        stats.put("totalSources", sources.size());
        stats.put("enabledSources", findEnabledSources().size());
        stats.put("activeAlerts", findActiveAlerts().size());
        return stats;
    }

    // Clear all data (for testing)
    public void clearAll() {
        users.clear();
        usersByEmail.clear();
        news.clear();
        newsByUrlHash.clear();
        alerts.clear();
        sources.clear();
        log.warn("All in-memory data cleared");
    }
}

