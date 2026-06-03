package com.stud.backend.dictionary.api;

public record CurrencyRef(
        String code,
        String name,
        String symbol,
        Boolean active
) {
}
