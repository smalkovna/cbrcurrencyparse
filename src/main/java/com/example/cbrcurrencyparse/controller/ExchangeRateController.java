package com.example.cbrcurrencyparse.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cbrcurrencyparse.model.ExchangeRateMessage;
import com.example.cbrcurrencyparse.service.ExchangeRateService;

@RestController
@RequestMapping("/exchange-rates")
public class ExchangeRateController 
{
    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) 
    {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping
    public ResponseEntity<ExchangeRateMessage> getExchangeRates() 
    {
        ExchangeRateMessage exchangeRates = exchangeRateService.getRates();
        return ResponseEntity.ok(exchangeRates);
    }
}
