package com.poke.api.controller;

import com.poke.api.dto.response.PokemonResponse;
import com.poke.api.dto.response.TypeSummaryResponse;
import com.poke.api.service.SearchHistoryService;
import com.poke.api.service.TypeService;
import com.poke.api.token.TokenRequired;
import com.poke.api.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/types")
@Tag(name = "Types", description = "Pokemon type endpoints")
public class TypeController {

    private final TypeService typeService;
    private final SearchHistoryService searchHistoryService;
    private final HttpServletRequest request;

    // Injects the type service, search history service, and request context.
    public TypeController(TypeService typeService, SearchHistoryService searchHistoryService, HttpServletRequest request) {
        this.typeService = typeService;
        this.searchHistoryService = searchHistoryService;
        this.request = request;
    }

    // Lists all Pokemon types.
    @GetMapping
    @TokenRequired
    @Operation(summary = "List Pokemon types", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<TypeSummaryResponse>>> getAllTypes() {
        String userIdentifier = getCurrentUserIdentifier();
        String username = getCurrentUsername();
        String endpoint = request.getRequestURI();
        try {
            List<TypeSummaryResponse> data = typeService.getAllTypes(username);
            searchHistoryService.saveSearchHistory(userIdentifier, "types", "", endpoint, true, HttpStatus.OK.value(), null);
            return ResponseEntity.ok(new ApiResponse<>(data));
        } catch (Exception ex) {
            searchHistoryService.saveSearchHistory(userIdentifier, "types", "", endpoint, false, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
            throw ex;
        }
    }

    // Lists Pokemon associated with a specific type.
    @GetMapping("/{name}/pokemon")
    @TokenRequired
    @Operation(summary = "List Pokemon by type", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<PokemonResponse>>> getPokemonByType(@PathVariable String name) {
        String userIdentifier = getCurrentUserIdentifier();
        String username = getCurrentUsername();
        String endpoint = request.getRequestURI();
        try {
            List<PokemonResponse> data = typeService.getPokemonByType(name, username);
            searchHistoryService.saveSearchHistory(userIdentifier, "types", name, endpoint, true, HttpStatus.OK.value(), null);
            return ResponseEntity.ok(new ApiResponse<>(data));
        } catch (Exception ex) {
            searchHistoryService.saveSearchHistory(userIdentifier, "types", name, endpoint, false, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
            throw ex;
        }
    }

    // Reads the current user identifier from the request context.
    private String getCurrentUserIdentifier() {
        Object authenticatedUserId = request.getAttribute("authenticatedUserId");
        if (authenticatedUserId != null) {
            return String.valueOf(authenticatedUserId);
        }
        return "anonymous";
    }

    // Reads the current username from the request context.
    private String getCurrentUsername() {
        Object authenticatedUserUsername = request.getAttribute("authenticatedUserUsername");
        if (authenticatedUserUsername instanceof String username && !username.isBlank()) {
            return username;
        }
        return "anonymous";
    }
}
