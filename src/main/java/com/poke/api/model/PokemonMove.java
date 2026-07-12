package com.poke.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entity for Pokemon move catalog.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "move")
public class PokemonMove {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "raw_json")
    private String rawJson;
}
