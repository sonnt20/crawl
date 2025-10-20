package com.crawl.chungkhoan.service;

import com.crawl.chungkhoan.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RateLimitService {

    // Map: userId -> lastCrawlTime
    private final Map<Long, LocalDateTime> userLastCrawlTime = new ConcurrentHashMap<>();

    /**
     * Check xem user có được phép crawl không
     * FREE: 30 phút/lần
     * PRO: 5 phút/lần
     * PREMIUM: 1 phút/lần
     */
    public boolean canCrawl(User user) {
        Long userId = user.getId();
        LocalDateTime now = LocalDateTime.now();
        
        // Lấy thời gian crawl cuối cùng
        LocalDateTime lastCrawl = userLastCrawlTime.get(userId);
        
        if (lastCrawl == null) {
            // Chưa crawl lần nào
            return true;
        }
        
        // Tính interval theo subscription tier
        int intervalMinutes = getIntervalMinutes(user.getSubscriptionTier());
        LocalDateTime nextAllowedTime = lastCrawl.plusMinutes(intervalMinutes);
        
        boolean canCrawl = now.isAfter(nextAllowedTime);
        
        if (!canCrawl) {
            log.warn("User {} (tier: {}) rate limited. Last crawl: {}, Next allowed: {}", 
                    user.getEmail(), user.getSubscriptionTier(), lastCrawl, nextAllowedTime);
        }
        
        return canCrawl;
    }

    /**
     * Ghi nhận thời gian crawl
     */
    public void recordCrawl(User user) {
        userLastCrawlTime.put(user.getId(), LocalDateTime.now());
        log.info("Recorded crawl for user {} (tier: {}) at {}", 
                user.getEmail(), user.getSubscriptionTier(), LocalDateTime.now());
    }

    /**
     * Lấy thời gian còn lại trước khi có thể crawl tiếp
     */
    public long getSecondsUntilNextCrawl(User user) {
        LocalDateTime lastCrawl = userLastCrawlTime.get(user.getId());
        
        if (lastCrawl == null) {
            return 0;
        }
        
        int intervalMinutes = getIntervalMinutes(user.getSubscriptionTier());
        LocalDateTime nextAllowedTime = lastCrawl.plusMinutes(intervalMinutes);
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isAfter(nextAllowedTime)) {
            return 0;
        }
        
        return java.time.Duration.between(now, nextAllowedTime).getSeconds();
    }

    /**
     * Lấy interval theo subscription tier
     */
    private int getIntervalMinutes(User.SubscriptionTier tier) {
        return switch (tier) {
            case FREE -> 30;      // 30 phút
            case PRO -> 5;        // 5 phút
            case PREMIUM -> 1;    // 1 phút
        };
    }

    /**
     * Reset rate limit cho user (admin only)
     */
    public void resetRateLimit(Long userId) {
        userLastCrawlTime.remove(userId);
        log.info("Reset rate limit for user {}", userId);
    }

    /**
     * Clear tất cả rate limits (admin only)
     */
    public void clearAllRateLimits() {
        userLastCrawlTime.clear();
        log.info("Cleared all rate limits");
    }
}

