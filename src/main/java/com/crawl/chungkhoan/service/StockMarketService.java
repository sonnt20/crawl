package com.crawl.chungkhoan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Service lấy dữ liệu thị trường chứng khoán từ API công khai
 * Sử dụng API của SSI (https://iboard.ssi.com.vn)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StockMarketService {

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Lấy dữ liệu cổ phiếu từ SSI iBoard API
     */
    public List<Map<String, Object>> getStockData() {
        List<Map<String, Object>> stocks = new ArrayList<>();
        
        try {
            // API SSI iBoard - Top stocks
            String url = "https://iboard-api.ssi.com.vn/statistics/charts/top-stocks?language=vi&lookupRequest.market=HOSE&lookupRequest.type=VALUE&lookupRequest.order=DESC";
            
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    JsonNode root = objectMapper.readTree(jsonData);
                    
                    // Parse data
                    if (root.has("data") && root.get("data").isArray()) {
                        JsonNode dataArray = root.get("data");
                        
                        int count = 0;
                        for (JsonNode item : dataArray) {
                            if (count >= 8) break; // Lấy 8 cổ phiếu
                            
                            Map<String, Object> stock = new HashMap<>();
                            stock.put("symbol", item.has("stockCode") ? item.get("stockCode").asText() : "");
                            stock.put("name", item.has("stockName") ? item.get("stockName").asText() : "");
                            stock.put("price", item.has("lastPrice") ? item.get("lastPrice").asDouble() : 0.0);
                            stock.put("change", item.has("priceChange") ? item.get("priceChange").asDouble() : 0.0);
                            stock.put("changePercent", item.has("percentPriceChange") ? item.get("percentPriceChange").asDouble() : 0.0);
                            stock.put("volume", item.has("totalVolume") ? item.get("totalVolume").asLong() : 0L);
                            
                            stocks.add(stock);
                            count++;
                        }
                    }
                    
                    log.info("Fetched {} stocks from SSI API", stocks.size());
                } else {
                    log.warn("SSI API returned non-successful response: {}", response.code());
                }
            }
            
        } catch (Exception e) {
            log.error("Error fetching stock data from SSI: {}", e.getMessage(), e);
            // Return mock data nếu API fail
            return getMockStockData();
        }
        
        // Nếu không có data, return mock
        if (stocks.isEmpty()) {
            return getMockStockData();
        }
        
        return stocks;
    }

    /**
     * Mock data backup khi API fail
     */
    private List<Map<String, Object>> getMockStockData() {
        List<Map<String, Object>> stocks = new ArrayList<>();
        
        stocks.add(createStock("VNM", "Vinamilk", 78.5, 1.2, 1.55, 2450000));
        stocks.add(createStock("VIC", "Vingroup", 45.3, -0.8, -1.73, 5680000));
        stocks.add(createStock("HPG", "Hòa Phát", 28.9, 0.5, 1.76, 8920000));
        stocks.add(createStock("VHM", "Vinhomes", 62.1, -1.2, -1.90, 3450000));
        stocks.add(createStock("TCB", "Techcombank", 52.8, 0.9, 1.73, 4230000));
        stocks.add(createStock("VPB", "VPBank", 18.4, 0.3, 1.66, 6780000));
        stocks.add(createStock("MSN", "Masan", 89.2, -2.1, -2.30, 1890000));
        stocks.add(createStock("FPT", "FPT Corp", 125.5, 3.5, 2.87, 2340000));
        
        return stocks;
    }

    /**
     * Lấy dữ liệu trái phiếu (mock data - API trái phiếu VN khó truy cập)
     */
    public List<Map<String, Object>> getBondData() {
        List<Map<String, Object>> bonds = new ArrayList<>();
        
        bonds.add(createStock("BOND001", "TP Chính phủ 5Y", 102.5, 0.2, 0.20, 150000));
        bonds.add(createStock("BOND002", "TP Chính phủ 10Y", 105.8, -0.1, -0.09, 230000));
        bonds.add(createStock("BOND003", "TP Doanh nghiệp VNM", 98.3, 0.5, 0.51, 89000));
        bonds.add(createStock("BOND004", "TP Doanh nghiệp VIC", 96.7, -0.3, -0.31, 120000));
        bonds.add(createStock("BOND005", "TP Chính phủ 3Y", 101.2, 0.1, 0.10, 180000));
        
        return bonds;
    }

    /**
     * Lấy dữ liệu chứng chỉ quỹ (mock data)
     */
    public List<Map<String, Object>> getFundData() {
        List<Map<String, Object>> funds = new ArrayList<>();
        
        funds.add(createStock("DCDS", "Quỹ DCDS", 15.8, 0.2, 1.28, 450000));
        funds.add(createStock("DCBC", "Quỹ DCBC", 12.3, -0.1, -0.81, 320000));
        funds.add(createStock("VFMVN30", "Quỹ VFM VN30", 18.9, 0.4, 2.16, 580000));
        funds.add(createStock("SSISCA", "Quỹ SSI SCA", 14.5, 0.3, 2.11, 290000));
        funds.add(createStock("VESAF", "Quỹ VESAF", 16.2, -0.2, -1.22, 410000));
        
        return funds;
    }

    /**
     * Helper method tạo stock object
     */
    private Map<String, Object> createStock(String symbol, String name, double price, 
                                           double change, double changePercent, long volume) {
        Map<String, Object> stock = new HashMap<>();
        stock.put("symbol", symbol);
        stock.put("name", name);
        stock.put("price", price);
        stock.put("change", change);
        stock.put("changePercent", changePercent);
        stock.put("volume", volume);
        return stock;
    }
}

