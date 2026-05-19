package com.stud.backend.dictionary.repository;

import com.stud.backend.dictionary.domain.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, String> {
    boolean existsByCodeIgnoreCase(String code);
}