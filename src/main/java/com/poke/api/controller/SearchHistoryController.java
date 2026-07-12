package com.poke.api.controller;

import com.poke.api.dto.response.SearchHistoryResponse;
import com.poke.api.service.SearchHistoryService;
import com.poke.api.token.TokenRequired;
import com.poke.api.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping
@Tag(name = "Search History", description = "Search history endpoints")
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    // Injects the search history service.
    public SearchHistoryController(SearchHistoryService searchHistoryService) {
        this.searchHistoryService = searchHistoryService;
    }

    // Lists the global search history without filters.
    public ResponseEntity<ApiResponse<List<SearchHistoryResponse>>> getGlobalSearchHistory() {
        return getGlobalSearchHistory(null, null, null, null);
    }

    // Lists the global search history with optional filters.
    @GetMapping("/search-history")
    @TokenRequired
    @Operation(summary = "List global search history", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<SearchHistoryResponse>>> getGlobalSearchHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String q) {
        return ResponseEntity.ok(new ApiResponse<>(searchHistoryService.getAllSearchHistory(from, to, type, q)));
    }

    // Lists the search history for a specific user.
    @GetMapping("/users/{userId}/search-history")
    @TokenRequired
    @Operation(summary = "List search history by user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<SearchHistoryResponse>>> getSearchHistoryByUser(@PathVariable String userId) {
        return ResponseEntity.ok(new ApiResponse<>(searchHistoryService.getSearchHistoryByUser(userId)));
    }
}
