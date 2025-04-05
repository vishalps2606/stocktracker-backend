package com.example.stocktracker.infrastructure.external;

import com.example.stocktracker.infrastructure.external.dto.AlphaVantageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class StockPriceFetcher {

    @Value("${alphavantage.api.url}")
    private String apiUrl;

    @Value("${alphavantage.api.key}")
    private String apiKey;

    private static final Map<String, BigDecimal> cache =
            new ConcurrentHashMap<>();

    public BigDecimal fetchStockPrice(String symbol) {

        if(cache.containsKey(symbol)) {
            log.info("Returning cached stock price for symbol: {}", symbol);
            return cache.get(symbol);
        }

        int maxRetries = 5;
        int delay = 2000;

        for(int attempt = 1; attempt <= maxRetries; attempt++) {
            try {

                log.info("Fetching stock price for symbol: {}", symbol);
                String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", apiUrl, symbol, apiKey);

                RestTemplate restTemplate = new RestTemplate();
                AlphaVantageResponse response = restTemplate.getForObject(url, AlphaVantageResponse.class);

                if (response == null || response.getGlobalQuote() == null
                        || response.getGlobalQuote().getPrice() == null) {
                    log.error("Invalid API response for symbol: {}", symbol);
                    throw new RuntimeException("Invalid API response");
                }
                BigDecimal price = response.getGlobalQuote().getPrice();
                log.info("Stock price for {}: {}", symbol, price);

                cache.put(symbol, price);
                return price;

            } catch (HttpClientErrorException.TooManyRequests e) {
                log.warn("429 Too many Requests. Retrying in {}second...", (delay * attempt)/1000);
                try {
                    Thread.sleep((long) delay *attempt);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted dureing retry delay", ex);
                }
            } catch (Exception e) {
                log.error("Failed to fetch stock price for {}: {}", symbol, e.getMessage());
            }
        }
        throw new RuntimeException("Failed to fetch stock price for symbol: " + symbol);
    }
}
