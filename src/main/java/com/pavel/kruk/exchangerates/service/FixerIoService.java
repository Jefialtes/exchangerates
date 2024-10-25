package com.pavel.kruk.exchangerates.service;

import com.pavel.kruk.exchangerates.dto.FixerIoResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class FixerIoService {

    private String apiKey;

    private String url;

    private RestTemplate restTemplate;

    public FixerIoService(@Value("${fixer.apiKey}") String apiKey,
                          @Value("${fixer.url}") String url,
                          RestTemplate restTemplate) {
        this.apiKey = apiKey;
        this.url = url;
        this.restTemplate = restTemplate;
    }

    public FixerIoResponseDto retrieveExchangeRates(String currencyCode) {
        String apiUrl = url.replace("{API_KEY}", apiKey);

//        currencyCode param is not used because on fixer.io it can be use only with paid account as base param
//        url.replace("{API_KEY}", apiKey).replace("{BASE_CURRENCY}", currencyCode);

        FixerIoResponseDto response = restTemplate.getForObject(apiUrl, FixerIoResponseDto.class);
        if (response != null && response.isSuccess()) {
            return response;
        }

        throw new RuntimeException("Failed to retrieve exchange rates from fixer.io");
    }
}