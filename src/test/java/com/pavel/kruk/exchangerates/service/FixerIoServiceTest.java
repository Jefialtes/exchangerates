package com.pavel.kruk.exchangerates.service;

import com.pavel.kruk.exchangerates.dto.FixerIoResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FixerIoServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FixerIoService fixerIoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fixerIoService = new FixerIoService("testApiKey", "https://api.fixer.io/latest?access_key={API_KEY}", restTemplate);
    }

    @Test
    void testRetrieveExchangeRates_Success() {
        FixerIoResponseDto mockResponse = new FixerIoResponseDto();
        mockResponse.setSuccess(true);

        when(restTemplate.getForObject(anyString(), eq(FixerIoResponseDto.class)))
                .thenReturn(mockResponse);

        FixerIoResponseDto response = fixerIoService.retrieveExchangeRates("EUR");

        assertNotNull(response);
        assertTrue(response.isSuccess());

        verify(restTemplate, times(1)).getForObject(anyString(), eq(FixerIoResponseDto.class));
    }

    @Test
    void testRetrieveExchangeRates_Failure() {
        FixerIoResponseDto mockResponse = new FixerIoResponseDto();
        mockResponse.setSuccess(false);

        when(restTemplate.getForObject(anyString(), eq(FixerIoResponseDto.class)))
                .thenReturn(mockResponse);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                fixerIoService.retrieveExchangeRates("EUR")
        );

        assertEquals("Failed to retrieve exchange rates from fixer.io", exception.getMessage());

        verify(restTemplate, times(1)).getForObject(anyString(), eq(FixerIoResponseDto.class));
    }

    @Test
    void testRetrieveExchangeRates_NullResponse() {
        when(restTemplate.getForObject(anyString(), eq(FixerIoResponseDto.class)))
                .thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                fixerIoService.retrieveExchangeRates("EUR")
        );

        assertEquals("Failed to retrieve exchange rates from fixer.io", exception.getMessage());

        verify(restTemplate, times(1)).getForObject(anyString(), eq(FixerIoResponseDto.class));
    }
}