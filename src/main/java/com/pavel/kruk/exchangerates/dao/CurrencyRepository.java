package com.pavel.kruk.exchangerates.dao;

import com.pavel.kruk.exchangerates.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Optional<Currency> findByCurrencyCode(String currencyCode);
}