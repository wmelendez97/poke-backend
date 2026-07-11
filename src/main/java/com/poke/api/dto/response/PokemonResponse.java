package com.poke.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Response DTO for Pokemon information.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PokemonResponse {
    private Long id;
    private String name;
    private String url;
    private Integer height;
    private Integer weight;
    private Integer baseExperience;
    private String imageUrl;
    private List<String> types;
    private List<AbilityResponse> abilities;
    private List<StatResponse> stats;
}
