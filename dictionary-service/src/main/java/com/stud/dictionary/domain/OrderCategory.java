package com.stud.dictionary.domain;


import com.stud.dictionary.common.persistence.BaseUuidEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "order_categories", schema = "bounty")
public class OrderCategory extends BaseUuidEntity {

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 140)
    private String slug;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean active;
}