package com.pavel.kruk.exchangerates.dao;

import com.pavel.kruk.exchangerates.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
}