package com.poke.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entity for Pokemon-type relationship.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pokemon_type_rel")
public class PokemonTypeRel {

    @EmbeddedId
    private PokemonTypeRelId id;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class PokemonTypeRelId implements java.io.Serializable {
        @Column(name = "pokemon_id")
        private Long pokemonId;

        @Column(name = "type_id")
        private Long typeId;
    }
}
