package com.crawl.chungkhoan.service;

import io.github.bonigarcia.wdm.WebDriverManager;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service lấy dữ liệu thị trường chứng khoán
 * Crawl từ CafeF bằng Selenium
 */
@Service
@Slf4j
public class StockMarketService {

    /**
     * Lấy dữ liệu cổ phiếu từ CafeF bằng Selenium
     */
    public List<Map<String, Object>> getStockData() {
        List<Map<String, Object>> stocks = new ArrayList<>();
        WebDriver driver = null;

        try {
            log.info("Setting up ChromeDriver for stock data...");
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");

            driver = new ChromeDriver(options);

            String url = "https://cafef.vn/";
            log.info("Loading CafeF homepage: {}", url);
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            // Tìm bảng top cổ phiếu (thường ở sidebar hoặc homepage)
            List<WebElement> stockRows = driver.findElements(By.cssSelector(
                "table.stock-table tr, " +
                ".stock-item, " +
                ".top-stock-item, " +
                "[class*='stock'] tr"
            ));

            log.info("Found {} potential stock elements", stockRows.size());

            int count = 0;
            for (WebElement row : stockRows) {
                if (count >= 8) break;

                try {
                    String text = row.getText();
                    if (text == null || text.trim().isEmpty()) continue;

                    // Parse text: "VNM Vinamilk 78.5 +1.2 +1.55%"
                    String[] parts = text.split("\\s+");
                    if (parts.length >= 3) {
                        String symbol = parts[0].trim();

                        // Kiểm tra symbol hợp lệ (3-4 ký tự chữ in hoa)
                        if (symbol.matches("^[A-Z]{3,4}$")) {
                            Map<String, Object> stock = new HashMap<>();
                            stock.put("symbol", symbol);
                            stock.put("name", parts.length > 1 ? parts[1] : symbol);
                            stock.put("price", parts.length > 2 ? parseDouble(parts[2]) : 0.0);
                            stock.put("change", parts.length > 3 ? parseDouble(parts[3]) : 0.0);
                            stock.put("changePercent", parts.length > 4 ? parseDouble(parts[4].replace("%", "")) : 0.0);
                            stock.put("volume", 0L);

                            stocks.add(stock);
                            count++;

                            log.debug("Parsed stock: {}", symbol);
                        }
                    }
                } catch (Exception e) {
                    log.debug("Error parsing stock element: {}", e.getMessage());
                }
            }

            log.info("Successfully crawled {} stocks from CafeF", stocks.size());

        } catch (Exception e) {
            log.error("Error crawling stock data with Selenium: {}", e.getMessage(), e);
        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception e) {
                    log.warn("Error closing driver: {}", e.getMessage());
                }
            }
        }

        if (stocks.isEmpty()) {
            log.warn("Stock data not available - returning empty list");
        }

        return stocks;
    }

    /**
     * Parse double từ string, trả về 0 nếu fail
     */
    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value.replace(",", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Parse long từ string, trả về 0 nếu fail
     */
    private long parseLong(String value) {
        try {
            return Long.parseLong(value.replace(",", "").replace(".", "").trim());
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * Lấy dữ liệu trái phiếu - Tạm thời không khả dụng
     */
    public List<Map<String, Object>> getBondData() {
        log.warn("Bond data not available - API not implemented");
        return new ArrayList<>();
    }

    /**
     * Lấy dữ liệu chứng chỉ quỹ - Tạm thời không khả dụng
     */
    public List<Map<String, Object>> getFundData() {
        log.warn("Fund data not available - API not implemented");
        return new ArrayList<>();
    }
}

