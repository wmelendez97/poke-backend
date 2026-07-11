package com.poke.api.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// DTO for detailed Pokemon information from PokeAPI. Contains fields like id, name, height, weight, base experience, sprites, types, abilities, stats, and moves.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokeApiPokemonDetailDto {
    private Long id;
    private String name;
    private Integer height;
    private Integer weight;
    @JsonProperty("base_experience")
    private Integer baseExperience;
    private Sprites sprites;
    private List<PokemonTypeSlot> types;
    private List<PokemonAbilitySlot> abilities;
    private List<PokemonStat> stats;
    private List<PokemonMoveSlot> moves;

    // Represents the sprites of a Pokemon.
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sprites {
        @JsonProperty("front_default")
        private String frontDefault;
    }

    // Represents a type slot for a Pokemon.
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PokemonTypeSlot {
        private Integer slot;
        private PokeApiNamedResource type;
    }

    // Represents an ability slot for a Pokemon.
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PokemonAbilitySlot {
        @JsonProperty("is_hidden")
        private Boolean isHidden;
        private Integer slot;
        private PokeApiNamedResource ability;
    }

    // Represents a stat for a Pokemon.
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PokemonStat {
        @JsonProperty("base_stat")
        private Integer baseStat;
        private Integer effort;
        private PokeApiNamedResource stat;
    }

    // Represents a move slot for a Pokemon.
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PokemonMoveSlot {
        private PokeApiNamedResource move;
        // Other fields like version_group_details can be added if needed
    }
}
