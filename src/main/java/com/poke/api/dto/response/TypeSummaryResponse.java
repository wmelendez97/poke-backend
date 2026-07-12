package com.poke.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Response DTO for the type catalog without nested Pokemon data.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypeSummaryResponse {
    private Long id;
    private String name;
}
