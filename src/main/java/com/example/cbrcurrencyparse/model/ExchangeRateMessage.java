package com.example.cbrcurrencyparse.model;

import java.time.LocalDate;
import java.util.List;

public class ExchangeRateMessage 
{
    private LocalDate date;
    private List<Currency> exchangeRates;
    
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public List<Currency> getExchangeRates() {
        return exchangeRates;
    }
    public void setExchangeRates(List<Currency> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((exchangeRates == null) ? 0 : exchangeRates.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExchangeRateMessage other = (ExchangeRateMessage) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (exchangeRates == null) {
            if (other.exchangeRates != null)
                return false;
        } else if (!exchangeRates.equals(other.exchangeRates))
            return false;
        return true;
    }

    
}
