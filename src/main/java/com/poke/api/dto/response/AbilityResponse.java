package com.poke.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Response DTO for Pokemon abilities.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AbilityResponse {
    private Long id;
    private String name;
    private Boolean isHidden;
    private Integer slot;
}
