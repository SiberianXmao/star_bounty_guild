package com.stud.backend.dictionary.repository;

import com.stud.backend.dictionary.domain.OrderCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderCategoryRepository extends JpaRepository<OrderCategory, UUID> {
    boolean existsBySlugIgnoreCase(String slug);
}
