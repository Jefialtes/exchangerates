package com.pavel.kruk.exchangerates.controller;

import com.pavel.kruk.exchangerates.entity.Currency;
import com.pavel.kruk.exchangerates.requestmodel.AddCurrencyRequest;
import com.pavel.kruk.exchangerates.service.CurrencyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/currencyExchange")
public class CurrencyExchangeController {

    private CurrencyService currencyService;

    public CurrencyExchangeController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/getAllCurrencies")
    public ResponseEntity<Set<String>> getAllCurrencies() {
        return currencyService.getAllCurrencies()
                .map(currencies -> new ResponseEntity<>(currencies, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @GetMapping("/getByCurrencyCode")
    public ResponseEntity<Currency> getAllExchangeRates(@RequestParam String currencyCode) {
        return findCurrencyByCode(currencyCode);
    }

    @PostMapping("/addCurrency")
    public ResponseEntity<Currency> addCurrency(@RequestBody AddCurrencyRequest requestBody) {
        currencyService.updateCurrency(requestBody.getCurrencyCode());
        return findCurrencyByCode(requestBody.getCurrencyCode());
    }

    private ResponseEntity<Currency> findCurrencyByCode(String currencyCode) {
        return currencyService.getCurrencyByCode(currencyCode)
                .map(currency -> new ResponseEntity<>(currency, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}