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

// Entity for Pokemon-ability relationship.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pokemon_ability")
public class PokemonAbilityRel {

    @EmbeddedId
    private PokemonAbilityRelId id;

    @Column(name = "is_hidden")
    private Boolean isHidden;

    private Integer slot;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class PokemonAbilityRelId implements java.io.Serializable {
        @Column(name = "pokemon_id")
        private Long pokemonId;

        @Column(name = "ability_id")
        private Long abilityId;
    }
}
