package com.pavel.kruk.exchangerates.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class FixerIoResponseDto {

    private boolean success;
    private long timestamp;
    private String base;
    private Map<String, BigDecimal> rates;
}