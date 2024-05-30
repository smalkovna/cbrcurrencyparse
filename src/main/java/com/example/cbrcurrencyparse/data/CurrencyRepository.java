package com.example.cbrcurrencyparse.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cbrcurrencyparse.model.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> 
{
    Optional<Currency> findByNumCode(Integer numCode);
}
