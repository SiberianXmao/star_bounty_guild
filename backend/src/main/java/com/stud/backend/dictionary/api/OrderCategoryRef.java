package com.stud.backend.dictionary.api;

import java.util.UUID;

public record OrderCategoryRef(
        UUID id,
        String name,
        String slug,
        Boolean active
) {
}
