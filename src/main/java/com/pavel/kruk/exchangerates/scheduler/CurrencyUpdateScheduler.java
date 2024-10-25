package com.pavel.kruk.exchangerates.scheduler;

import com.pavel.kruk.exchangerates.service.CurrencyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CurrencyUpdateScheduler {

    private CurrencyService currencyService;

    public CurrencyUpdateScheduler(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void updateCurrencyRates() {
        currencyService.updateCurrencyRates();
    }
}