package com.crawl.chungkhoan.controller;

import com.crawl.chungkhoan.model.User;
import com.crawl.chungkhoan.service.CrawlService;
import com.crawl.chungkhoan.service.SeleniumCrawlService;
import com.crawl.chungkhoan.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/crawl")
@RequiredArgsConstructor
@Slf4j
public class CrawlController {

    private final CrawlService crawlService;
    private final SeleniumCrawlService seleniumCrawlService;
    private final RateLimitService rateLimitService;

    /**
     * Trigger crawl manually - Chỉ admin - Dùng Selenium
     */
    @PostMapping("/trigger")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> triggerCrawl() {
        log.info("Manual Selenium crawl triggered by admin");

        try {
            // Chạy async để không block request
            new Thread(() -> seleniumCrawlService.crawlAllSources()).start();

            Map<String, String> response = new HashMap<>();
            response.put("message", "Selenium crawl started successfully");
            response.put("status", "RUNNING");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error triggering Selenium crawl: {}", e.getMessage());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Error starting crawl: " + e.getMessage());
            response.put("status", "ERROR");

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Trigger crawl cho user - Theo subscription plan với rate limiting
     */
    @PostMapping("/user-trigger")
    public ResponseEntity<Map<String, Object>> userTriggerCrawl(@AuthenticationPrincipal User user) {
        log.info("User crawl triggered by: {} (tier: {})", user.getEmail(), user.getSubscriptionTier());

        try {
            // Check rate limit
            if (!rateLimitService.canCrawl(user)) {
                long secondsUntilNext = rateLimitService.getSecondsUntilNextCrawl(user);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Vui lòng đợi " + (secondsUntilNext / 60) + " phút nữa để crawl tiếp");
                response.put("status", "RATE_LIMITED");
                response.put("secondsUntilNext", secondsUntilNext);
                response.put("tier", user.getSubscriptionTier().toString());

                log.warn("User {} rate limited. {} seconds until next crawl", user.getEmail(), secondsUntilNext);

                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
            }

            // Record crawl time
            rateLimitService.recordCrawl(user);

            // Start Selenium crawl async
            new Thread(() -> seleniumCrawlService.crawlAllSources()).start();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Selenium crawl started successfully");
            response.put("status", "RUNNING");
            response.put("tier", user.getSubscriptionTier().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error triggering user crawl: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error starting crawl: " + e.getMessage());
            response.put("status", "ERROR");

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get crawl status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getCrawlStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "IDLE");
        response.put("lastCrawl", null);
        response.put("nextCrawl", null);
        
        return ResponseEntity.ok(response);
    }
}

