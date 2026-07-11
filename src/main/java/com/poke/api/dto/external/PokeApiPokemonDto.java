package com.poke.api.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// DTO for listing Pokemon from PokeAPI. Contains a count, next/previous URLs, and a list of named resources (Pokemon).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokeApiPokemonDto {
    private Integer count;
    private String next;
    private String previous;
    private List<PokeApiNamedResource> results;
}
