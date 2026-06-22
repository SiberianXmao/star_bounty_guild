package com.stud.dictionary.repository;

import com.stud.dictionary.domain.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, String> {
    boolean existsByCodeIgnoreCase(String code);
}