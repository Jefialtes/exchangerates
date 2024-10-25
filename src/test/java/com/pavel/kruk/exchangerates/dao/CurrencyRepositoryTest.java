package com.pavel.kruk.exchangerates.dao;

import com.pavel.kruk.exchangerates.entity.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Test
    @DisplayName("Test findByCurrencyCode when currency exists")
    void testFindByCurrencyCode_WhenCurrencyExists() {
        Currency currency = new Currency();
        currency.setCurrencyCode("USD");
        currencyRepository.save(currency);

        Optional<Currency> result = currencyRepository.findByCurrencyCode("USD");

        assertTrue(result.isPresent(), "Currency should be found");
        assertEquals("USD", result.get().getCurrencyCode(), "Currency code should be USD");
    }

    @Test
    @DisplayName("Test findByCurrencyCode when currency does not exist")
    void testFindByCurrencyCode_WhenCurrencyDoesNotExist() {
        Optional<Currency> result = currencyRepository.findByCurrencyCode("EUR");

        assertFalse(result.isPresent(), "Currency should not be found");
    }
}