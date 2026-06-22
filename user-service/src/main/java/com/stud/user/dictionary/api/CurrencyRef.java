package com.stud.user.dictionary.api;

public record CurrencyRef(
        String code,
        String name,
        String symbol,
        Boolean active
) {
}
