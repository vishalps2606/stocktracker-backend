package com.example.stocktracker.application.services;

import com.example.stocktracker.domain.models.Stock;
import com.example.stocktracker.infrastructure.repositories.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public Stock saveStock(String symbol, BigDecimal price) {

        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setPrice(price);
        stock.setTimestamp(LocalDateTime.now());

        return stockRepository.save(stock);
    }

    public List<Stock> getStockHistory(String symbol) {
        return stockRepository.findBySymbolOrderByTimestampDesc(symbol);
    }
}
