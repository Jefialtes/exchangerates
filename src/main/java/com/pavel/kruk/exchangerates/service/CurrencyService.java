package com.pavel.kruk.exchangerates.service;

import com.pavel.kruk.exchangerates.dao.CurrencyRepository;
import com.pavel.kruk.exchangerates.dto.FixerIoResponseDto;
import com.pavel.kruk.exchangerates.entity.Currency;
import com.pavel.kruk.exchangerates.entity.ExchangeRate;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CurrencyService {

    private final Map<String, Currency> currencyExchangeRates;

    private CurrencyRepository currencyRepository;

    private FixerIoService fixerIoService;

    public CurrencyService(Map<String, Currency> currencyExchangeRates, CurrencyRepository currencyRepository, FixerIoService fixerIoService) {
        this.currencyExchangeRates = currencyExchangeRates;
        this.currencyRepository = currencyRepository;
        this.fixerIoService = fixerIoService;
    }

    public Optional<Currency> getCurrencyByCode(String currencyCode) {
        return Optional.ofNullable(currencyExchangeRates.computeIfAbsent(currencyCode, this::getAndCacheCurrencyByCode));
    }

    @Transactional
    public Currency getAndCacheCurrencyByCode(String currencyCode) {
        Optional<Currency> currency = currencyRepository.findByCurrencyCode(currencyCode);
        return currency.orElse(null);
    }

    public Optional<Set<String>> getAllCurrencies() {
        return Optional.of(currencyExchangeRates.keySet());
    }

    public void updateCurrency(String currencyCode) {
        FixerIoResponseDto fixerIoCurrency = fixerIoService.retrieveExchangeRates(currencyCode);
        saveOrUpdateCurrencies(fixerIoCurrency);
    }

    @Transactional
    public void saveOrUpdateCurrencies(FixerIoResponseDto fixerIoCurrency) {
        currencyRepository.findByCurrencyCode(fixerIoCurrency.getBase())
                .ifPresentOrElse(existingCurrency -> {
                    existingCurrency.setUpdateTime(fixerIoCurrency.getTimestamp());
                    Map<String, BigDecimal> rates = fixerIoCurrency.getRates();
                    for (ExchangeRate exchangeRate : existingCurrency.getExchangeRates()) {
                        BigDecimal rate = rates.get(exchangeRate.getCurrencyCode());
                        exchangeRate.setRate(rate);
                    }
                    currencyRepository.save(existingCurrency);
                }, () -> {
                    Currency newCurrency = createCurrency(fixerIoCurrency);
                    currencyRepository.save(newCurrency);
                });
    }

    public void updateCurrencyRates() {
        for (String currencyRate : currencyExchangeRates.keySet()) {
            updateCurrency(currencyRate);
        }
    }

    private Currency createCurrency(FixerIoResponseDto response) {
        Currency currency = new Currency();
        String currencyCode = response.getBase();
        currency.setCurrencyCode(currencyCode);

        List<ExchangeRate> exchangeRates = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : response.getRates().entrySet()) {
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setCurrency(currency);
            exchangeRate.setCurrencyCode(entry.getKey());
            exchangeRate.setRate(entry.getValue());
            exchangeRates.add(exchangeRate);
        }

        currency.setExchangeRates(exchangeRates);
        return currency;
    }
}