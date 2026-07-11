package com.poke.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Response DTO for Pokemon moves.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveResponse {
    private Long id;
    private String name;
}
