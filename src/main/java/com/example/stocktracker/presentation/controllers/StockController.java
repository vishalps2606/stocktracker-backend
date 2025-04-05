package com.example.stocktracker.presentation.controllers;

import com.example.stocktracker.application.services.StockService;
import com.example.stocktracker.infrastructure.external.StockPriceFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final StockPriceFetcher stockPriceFetcher;

    @GetMapping("/{symbol}")
    public BigDecimal getStockPrice(@PathVariable String symbol) {
        return stockPriceFetcher.fetchStockPrice(symbol);
    }

    @PostMapping("/{symbol}")
    public void saveStock(@PathVariable String symbol) {
        BigDecimal price = stockPriceFetcher.fetchStockPrice(symbol);
        stockService.saveStock(symbol, price);
    }

    @GetMapping("/history/{symbol}")
    public List<?> getStockHistory(@PathVariable String symbol) {
        return stockService.getStockHistory(symbol);
    }
}
