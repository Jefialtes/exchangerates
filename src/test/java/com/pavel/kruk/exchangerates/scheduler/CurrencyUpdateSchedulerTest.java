package com.pavel.kruk.exchangerates.scheduler;

import com.pavel.kruk.exchangerates.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.Mockito.verify;

@SpringBootTest
@SpringJUnitConfig
public class CurrencyUpdateSchedulerTest {

    @MockBean
    private CurrencyService currencyService;

    private CurrencyUpdateScheduler currencyUpdateScheduler;

    @BeforeEach
    void setUp() {
        currencyUpdateScheduler = new CurrencyUpdateScheduler(currencyService);
    }

    @Test
    void updateCurrencyRates_shouldInvokeCurrencyService() {
        currencyUpdateScheduler.updateCurrencyRates();
        verify(currencyService).updateCurrencyRates();
    }
}