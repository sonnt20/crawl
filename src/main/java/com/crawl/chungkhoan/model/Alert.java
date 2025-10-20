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
public class Alert {

    private Long id;
    private Long userId;
    private String keyword;

    @Builder.Default
    private Boolean isRegex = false;

    @Builder.Default
    private Boolean caseSensitive = false;

    private AlertType alertType;
    private String webhookUrl;
    private String telegramChatId;

    @Builder.Default
    private boolean enabled = true;

    private LocalDateTime lastTriggeredAt;
    private LocalDateTime createdAt;

    public enum AlertType {
        EMAIL, TELEGRAM, WEBHOOK
    }
}

