package com.crawl.chungkhoan.controller;

import com.crawl.chungkhoan.service.StockMarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
@Slf4j
public class MarketController {

    private final StockMarketService stockMarketService;

    /**
     * Lấy dữ liệu cổ phiếu
     */
    @GetMapping("/stocks")
    public ResponseEntity<Map<String, Object>> getStocks() {
        log.info("Fetching stock market data");
        
        try {
            List<Map<String, Object>> stocks = stockMarketService.getStockData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", stocks);
            response.put("count", stocks.size());
            response.put("type", "stocks");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching stocks: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Lấy dữ liệu trái phiếu
     */
    @GetMapping("/bonds")
    public ResponseEntity<Map<String, Object>> getBonds() {
        log.info("Fetching bond market data");
        
        try {
            List<Map<String, Object>> bonds = stockMarketService.getBondData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", bonds);
            response.put("count", bonds.size());
            response.put("type", "bonds");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching bonds: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Lấy dữ liệu chứng chỉ quỹ
     */
    @GetMapping("/funds")
    public ResponseEntity<Map<String, Object>> getFunds() {
        log.info("Fetching fund market data");
        
        try {
            List<Map<String, Object>> funds = stockMarketService.getFundData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", funds);
            response.put("count", funds.size());
            response.put("type", "funds");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching funds: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Lấy tất cả dữ liệu thị trường
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllMarketData() {
        log.info("Fetching all market data");
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("stocks", stockMarketService.getStockData());
            response.put("bonds", stockMarketService.getBondData());
            response.put("funds", stockMarketService.getFundData());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching all market data: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

