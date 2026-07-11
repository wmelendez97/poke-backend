package com.poke.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Response DTO for Pokemon stats.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatResponse {
    private String name;
    private Integer baseStat;
    private Integer effort;
}
