package com.poke.api.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// DTO for ability details from PokeAPI.
// Includes ability ID, name, and a list of associated Pokémon.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokeApiAbilityDto {
    private Long id;
    private String name;
    private List<PokemonSlot> pokemon;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PokemonSlot {
        private Boolean isHidden;
        private PokeApiNamedResource pokemon;
        private Integer slot;
    }
}