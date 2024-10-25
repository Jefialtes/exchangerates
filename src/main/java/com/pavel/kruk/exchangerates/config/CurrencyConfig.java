package com.pavel.kruk.exchangerates.config;

import com.pavel.kruk.exchangerates.entity.Currency;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CurrencyConfig {

    @Bean
    public Map<String, Currency> currencyExchangeRates() {
        return new HashMap<>();
    }
}