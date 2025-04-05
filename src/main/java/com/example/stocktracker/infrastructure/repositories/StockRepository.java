package com.example.stocktracker.infrastructure.repositories;

import com.example.stocktracker.domain.models.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findBySymbolOrderByTimestampDesc(String symbol);
}
