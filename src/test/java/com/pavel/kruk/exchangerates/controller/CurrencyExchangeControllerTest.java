package com.pavel.kruk.exchangerates.controller;

import com.pavel.kruk.exchangerates.entity.Currency;
import com.pavel.kruk.exchangerates.entity.ExchangeRate;
import com.pavel.kruk.exchangerates.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyExchangeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Test
    void getAllCurrencies() throws Exception {
        Set<String> currencies = Set.of("EUR");
        when(currencyService.getAllCurrencies()).thenReturn(Optional.of(currencies));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/currencyExchange/getAllCurrencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("EUR"));
    }

    @Test
    void getByCurrencyCode() throws Exception {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        ExchangeRate exchangeRate = new ExchangeRate(1L, new Currency(), "USD", new BigDecimal("0.9"));
        exchangeRates.add(exchangeRate);
        Currency currency = new Currency(1L, "EUR", 15888888L, exchangeRates);
        when(currencyService.getCurrencyByCode("EUR")).thenReturn(Optional.of(currency));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/currencyExchange/getByCurrencyCode?currencyCode=EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyCode").value("EUR"))
                .andExpect(jsonPath("$.updateTime").value(15888888))
                .andExpect(jsonPath("$.exchangeRates[0].currencyCode").value("USD"))
                .andExpect(jsonPath("$.exchangeRates[0].rate").value(0.9));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/currencyExchange/getByCurrencyCode?currencyCode=USD"))
                .andExpect(status().isNoContent());
    }

    @Test
    void addCurrency() throws Exception {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        ExchangeRate exchangeRate = new ExchangeRate(1L, new Currency(), "USD", new BigDecimal("0.9"));
        exchangeRates.add(exchangeRate);
        Currency currency = new Currency(1L, "EUR", 15888888L, exchangeRates);
        when(currencyService.getCurrencyByCode("EUR")).thenReturn(Optional.of(currency));

        String jsonRequest = """
            {
                "currencyCode": "EUR"
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/currencyExchange/addCurrency"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/currencyExchange/addCurrency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyCode").value("EUR"))
                .andExpect(jsonPath("$.updateTime").value(15888888))
                .andExpect(jsonPath("$.exchangeRates[0].currencyCode").value("USD"))
                .andExpect(jsonPath("$.exchangeRates[0].rate").value(0.9));
    }
}