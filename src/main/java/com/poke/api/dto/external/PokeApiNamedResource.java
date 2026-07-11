package com.poke.api.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Represents a named API resource from PokeAPI. This is a generic DTO for resources that have a name and a URL.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokeApiNamedResource {
    private String name;
    private String url;
}
