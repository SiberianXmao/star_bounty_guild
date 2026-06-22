package com.stud.user.dictionary.api;

import java.util.UUID;

public record OrderCategoryRef(
        UUID id,
        String name,
        String slug,
        Boolean active
) {
}
