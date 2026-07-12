package com.poke.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entity for Pokemon-move relationship.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pokemon_move")
public class PokemonMoveRel {

    @EmbeddedId
    private PokemonMoveRelId id;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class PokemonMoveRelId implements java.io.Serializable {
        @Column(name = "pokemon_id")
        private Long pokemonId;

        @Column(name = "move_id")
        private Long moveId;
    }
}
