package com.poke.api.controller;

import com.poke.api.dto.response.PokemonPageResponse;
import com.poke.api.dto.response.PokemonResponse;
import com.poke.api.token.TokenRequired;
import com.poke.api.service.PokemonService;
import com.poke.api.service.SearchHistoryService;
import com.poke.api.util.ApiError;
import com.poke.api.util.ApiMessages;
import com.poke.api.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.List;

@RestController
@RequestMapping("/pokemon")
@Validated
@Tag(name = "Pokemon", description = "Pokemon endpoints")
public class PokemonController {

    private final PokemonService pokemonService;
    private final SearchHistoryService searchHistoryService;
    private final HttpServletRequest request;

    // Injects the Pokemon service, search history service, and HttpServletRequest.
    public PokemonController(PokemonService pokemonService, SearchHistoryService searchHistoryService, HttpServletRequest request) {
        this.pokemonService = pokemonService;
        this.searchHistoryService = searchHistoryService;
        this.request = request;
    }

    // Returns a paginated Pokemon list.
    @GetMapping
    @TokenRequired
    @Operation(summary = "List Pokemon with pagination", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PokemonPageResponse>> getPokemonPage(
            @RequestParam(defaultValue = "20") @Min(1) int limit,
            @RequestParam(defaultValue = "0") @Min(0) int offset) {
        String userIdentifier = getCurrentUserIdentifier();
        String username = getCurrentUsername();
        String endpoint = request.getRequestURI();
        String queryValue = "limit=" + limit + "&offset=" + offset;

        try {
            PokemonPageResponse data = pokemonService.getPokemonPage(limit, offset);
            searchHistoryService.saveSearchHistory(userIdentifier, "pagination", queryValue, endpoint, true, HttpStatus.OK.value(), null);
            return ResponseEntity.ok(new ApiResponse<>(data));
        } catch (ResourceAccessException ex) {
            searchHistoryService.saveSearchHistory(userIdentifier, "pagination", queryValue, endpoint, false, HttpStatus.GATEWAY_TIMEOUT.value(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(new ApiResponse<>(ApiMessages.ERROR_EXTERNAL_SERVICE_TIMEOUT.getMessage(),
                            List.of(ApiError.ErrorCodes.EXTERNAL_SERVICE_TIMEOUT), HttpStatus.GATEWAY_TIMEOUT));
        } catch (RestClientException ex) {
            searchHistoryService.saveSearchHistory(userIdentifier, "pagination", queryValue, endpoint, false, HttpStatus.BAD_GATEWAY.value(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new ApiResponse<>(ApiMessages.ERROR_EXTERNAL_SERVICE.getMessage(),
                            List.of(ApiError.ErrorCodes.EXTERNAL_SERVICE_ERROR), HttpStatus.BAD_GATEWAY));
        } catch (Exception ex) {
            searchHistoryService.saveSearchHistory(userIdentifier, "pagination", queryValue, endpoint, false, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
            throw ex;
        }
    }

    // Returns the Pokemon detail by ID or name.
    @GetMapping("/{idOrName}")
    @TokenRequired
    @Operation(summary = "Get Pokemon detail by ID or name", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PokemonResponse>> getPokemonByIdOrName(@PathVariable String idOrName) {
        String userIdentifier = getCurrentUserIdentifier();
        String username = getCurrentUsername();
        String endpoint = request.getRequestURI();
        String queryValue = idOrName;

        try {
            PokemonResponse data = pokemonService.getPokemonByIdOrName(idOrName, username);
            searchHistoryService.saveSearchHistory(userIdentifier, "detail", queryValue, endpoint, true, HttpStatus.OK.value(), null);
            return ResponseEntity.ok(new ApiResponse<>(data));
        } catch (ResourceAccessException ex) {
            searchHistoryService.saveSearchHistory(userIdentifier, "detail", queryValue, endpoint, false, HttpStatus.GATEWAY_TIMEOUT.value(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(new ApiResponse<>(ApiMessages.ERROR_EXTERNAL_SERVICE_TIMEOUT.getMessage(),
                            List.of(ApiError.ErrorCodes.EXTERNAL_SERVICE_TIMEOUT), HttpStatus.GATEWAY_TIMEOUT));
        } catch (RestClientException ex) {
            searchHistoryService.saveSearchHistory(userIdentifier, "detail", queryValue, endpoint, false, HttpStatus.BAD_GATEWAY.value(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new ApiResponse<>(ApiMessages.ERROR_EXTERNAL_SERVICE.getMessage(),
                            List.of(ApiError.ErrorCodes.EXTERNAL_SERVICE_ERROR), HttpStatus.BAD_GATEWAY));
        } catch (Exception ex) {
            searchHistoryService.saveSearchHistory(userIdentifier, "detail", queryValue, endpoint, false, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
            throw ex;
        }
    }

    // Searches Pokemon by exact or partial name.
    @GetMapping("/search")
    @TokenRequired
    @Operation(summary = "Search Pokemon by exact or partial name", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<PokemonResponse>>> searchPokemonByName(@RequestParam String name) {
        String userIdentifier = getCurrentUserIdentifier();
        String username = getCurrentUsername();
        String endpoint = request.getRequestURI();
        String queryValue = name;

        try {
            List<PokemonResponse> data = pokemonService.searchPokemonByName(name, username);
            searchHistoryService.saveSearchHistory(userIdentifier, "search", queryValue, endpoint, true, HttpStatus.OK.value(), null);
            return ResponseEntity.ok(new ApiResponse<>(data));
        } catch (ResourceAccessException ex) {
            searchHistoryService.saveSearchHistory(userIdentifier, "search", queryValue, endpoint, false, HttpStatus.GATEWAY_TIMEOUT.value(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(new ApiResponse<>(ApiMessages.ERROR_EXTERNAL_SERVICE_TIMEOUT.getMessage(),
                            List.of(ApiError.ErrorCodes.EXTERNAL_SERVICE_TIMEOUT), HttpStatus.GATEWAY_TIMEOUT));
        } catch (RestClientException ex) {
            searchHistoryService.saveSearchHistory(userIdentifier, "search", queryValue, endpoint, false, HttpStatus.BAD_GATEWAY.value(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new ApiResponse<>(ApiMessages.ERROR_EXTERNAL_SERVICE.getMessage(),
                            List.of(ApiError.ErrorCodes.EXTERNAL_SERVICE_ERROR), HttpStatus.BAD_GATEWAY));
        } catch (Exception ex) {
            searchHistoryService.saveSearchHistory(userIdentifier, "search", queryValue, endpoint, false, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
            throw ex;
        }
    }

    // Retrieves the current user's identifier.
    private String getCurrentUserIdentifier() {
        Object authenticatedUserId = request.getAttribute("authenticatedUserId");
        if (authenticatedUserId != null) {
            return String.valueOf(authenticatedUserId);
        }
        return "anonymous";
    }

    // Returns the current user's username for audit fields and cache writes.
    private String getCurrentUsername() {
        String authenticatedUserUsername = (String) request.getAttribute("authenticatedUserUsername");
        if (authenticatedUserUsername != null && !authenticatedUserUsername.isBlank()) {
            return authenticatedUserUsername;
        }
        return "anonymous";
    }
}
