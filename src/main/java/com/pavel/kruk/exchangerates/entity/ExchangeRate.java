package com.pavel.kruk.exchangerates.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name="exchange_rate")
@Data
public class ExchangeRate {

    public ExchangeRate() {
    }

    public ExchangeRate(Long id, Currency currency, String currencyCode, BigDecimal rate) {
        this.id = id;
        this.currency = currency;
        this.currencyCode = currencyCode;
        this.rate = rate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private Long id;

    @ManyToOne
    @JoinColumn(name = "changeable_currency_id")
    @JsonIgnore
    private Currency currency;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "rate")
    private BigDecimal rate;
}