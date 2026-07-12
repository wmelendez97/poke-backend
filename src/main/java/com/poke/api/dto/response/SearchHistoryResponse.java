package com.poke.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Response DTO for search history entries.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryResponse {
    private Long id;
    private Long userId;
    private String userIdentifier;
    private String searchType;
    private String queryValue;
    private String endpoint;
    private Boolean success;
    private Integer statusCode;
    private String errorMessage;
    private LocalDateTime createdAt;
}
