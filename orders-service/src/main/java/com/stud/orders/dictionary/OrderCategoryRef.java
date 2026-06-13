package com.stud.orders.dictionary;

import java.util.UUID;

public record OrderCategoryRef(UUID id, String name, String slug, Boolean active) {
}
