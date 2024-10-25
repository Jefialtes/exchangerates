package com.pavel.kruk.exchangerates.service;

import com.pavel.kruk.exchangerates.dao.CurrencyRepository;
import com.pavel.kruk.exchangerates.dto.FixerIoResponseDto;
import com.pavel.kruk.exchangerates.entity.Currency;
import com.pavel.kruk.exchangerates.entity.ExchangeRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {

    @Mock
    private Map<String, Currency> currencyExchangeRates;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private FixerIoService fixerIoService;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCurrencyByCode_WhenCurrencyExists() {
        String currencyCode = "USD";
        Currency currency = new Currency();
        currency.setCurrencyCode(currencyCode);
        when(currencyExchangeRates.computeIfAbsent(eq(currencyCode), any())).thenReturn(currency);

        Optional<Currency> result = currencyService.getCurrencyByCode(currencyCode);

        assertTrue(result.isPresent());
        assertEquals(currency, result.get());
    }

    @Test
    void testGetCurrencyByCode_WhenCurrencyDoesNotExist() {
        String currencyCode = "USD";
        when(currencyExchangeRates.computeIfAbsent(eq(currencyCode), any())).thenReturn(null);

        Optional<Currency> result = currencyService.getCurrencyByCode(currencyCode);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetAndCacheCurrencyByCode_WhenCurrencyExistsInRepository() {
        String currencyCode = "USD";
        Currency currency = new Currency();
        currency.setCurrencyCode(currencyCode);
        when(currencyRepository.findByCurrencyCode(currencyCode)).thenReturn(Optional.of(currency));

        Currency result = currencyService.getAndCacheCurrencyByCode(currencyCode);

        assertNotNull(result);
        assertEquals(currency, result);
    }

    @Test
    void testGetAndCacheCurrencyByCode_WhenCurrencyDoesNotExistInRepository() {
        String currencyCode = "USD";
        when(currencyRepository.findByCurrencyCode(currencyCode)).thenReturn(Optional.empty());

        Currency result = currencyService.getAndCacheCurrencyByCode(currencyCode);

        assertNull(result);
    }

    @Test
    void testGetAllCurrencies() {
        Set<String> currencies = new HashSet<>(Arrays.asList("USD", "EUR"));
        when(currencyExchangeRates.keySet()).thenReturn(currencies);

        Optional<Set<String>> result = currencyService.getAllCurrencies();

        assertTrue(result.isPresent());
        assertEquals(currencies, result.get());
    }

    @Test
    void testUpdateCurrency() {
        String currencyCode = "USD";
        FixerIoResponseDto responseDto = new FixerIoResponseDto();
        responseDto.setBase(currencyCode);
        responseDto.setTimestamp(new Date().getTime());
        responseDto.setRates(Map.of("EUR", BigDecimal.valueOf(0.85)));

        when(fixerIoService.retrieveExchangeRates(currencyCode)).thenReturn(responseDto);

        currencyService.updateCurrency(currencyCode);

        verify(fixerIoService, times(1)).retrieveExchangeRates(currencyCode);
        verify(currencyRepository, times(1)).save(any(Currency.class));
    }

    @Test
    void testSaveOrUpdateCurrencies_WhenCurrencyExists() {
        FixerIoResponseDto responseDto = new FixerIoResponseDto();
        responseDto.setBase("USD");
        responseDto.setTimestamp(new Date().getTime());
        responseDto.setRates(Map.of("EUR", BigDecimal.valueOf(0.85)));

        Currency existingCurrency = new Currency();
        existingCurrency.setCurrencyCode("USD");
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setCurrencyCode("EUR");
        existingCurrency.setExchangeRates(List.of(exchangeRate));

        when(currencyRepository.findByCurrencyCode("USD")).thenReturn(Optional.of(existingCurrency));

        currencyService.saveOrUpdateCurrencies(responseDto);

        assertEquals(BigDecimal.valueOf(0.85), exchangeRate.getRate());
        verify(currencyRepository, times(1)).save(existingCurrency);
    }

    @Test
    void testSaveOrUpdateCurrencies_WhenCurrencyDoesNotExist() {
        FixerIoResponseDto responseDto = new FixerIoResponseDto();
        responseDto.setBase("USD");
        responseDto.setTimestamp(new Date().getTime());
        responseDto.setRates(Map.of("EUR", BigDecimal.valueOf(0.85)));

        when(currencyRepository.findByCurrencyCode("USD")).thenReturn(Optional.empty());

        currencyService.saveOrUpdateCurrencies(responseDto);

        verify(currencyRepository, times(1)).save(any(Currency.class));
    }

    @Test
    void testUpdateCurrencyRates() {
        Set<String> currencies = new HashSet<>(Arrays.asList("USD"));
        when(currencyExchangeRates.keySet()).thenReturn(currencies);

        FixerIoResponseDto responseDto = new FixerIoResponseDto();
        responseDto.setBase("USD");
        responseDto.setTimestamp(new Date().getTime());
        responseDto.setRates(Map.of("EUR", BigDecimal.valueOf(0.85)));

        when(fixerIoService.retrieveExchangeRates("USD")).thenReturn(responseDto);

        currencyService.updateCurrencyRates();

        verify(fixerIoService, times(1)).retrieveExchangeRates("USD");
    }
}