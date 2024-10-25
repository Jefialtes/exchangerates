package com.pavel.kruk.exchangerates.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "currency")
@Data
public class Currency {

    public Currency() {
    }

    public Currency(Long id, String currencyCode, Long updateTime, List<ExchangeRate> exchangeRates) {
        this.id = id;
        this.currencyCode = currencyCode;
        this.updateTime = updateTime;
        this.exchangeRates = exchangeRates;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private Long id;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "update_time")
    private Long updateTime;

    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ExchangeRate> exchangeRates;
}