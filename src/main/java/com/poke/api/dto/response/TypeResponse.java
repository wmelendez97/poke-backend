package com.poke.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Response DTO for Pokemon types.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypeResponse {
    private Long id;
    private String name;
    private List<PokemonResponse> pokemon;
}
