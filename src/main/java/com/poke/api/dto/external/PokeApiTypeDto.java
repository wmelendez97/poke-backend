package com.poke.api.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// DTO for listing types from PokeAPI.
// Contains a count, next/previous URLs, and a list of named resources (types).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokeApiTypeDto {
    private Integer count;
    private String next;
    private String previous;
    private List<PokeApiNamedResource> results;
}