package com.poke.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PokemonPageResponse {
    private List<PokemonPageItemResponse> content;
    private Integer totalPages;
    private Integer totalElements;
    private Integer numberOfElements;
    private Integer size;
}
