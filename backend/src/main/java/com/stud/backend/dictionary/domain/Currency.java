package com.stud.backend.dictionary.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "currencies", schema = "bounty")
public class Currency {

    @Id
    @Column(name = "code", nullable = false, length = 16)
    private String code;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Column(name = "symbol", length = 16)
    private String symbol;

    @Column(name = "is_active", nullable = false)
    private Boolean active;
}