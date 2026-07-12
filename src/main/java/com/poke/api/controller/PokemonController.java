package com.poke.api.controller;

import com.poke.api.dto.response.PokemonPageResponse;
import com.poke.api.dto.response.PokemonResponse;
import com.poke.api.token.TokenRequired;
import com.poke.api.service.PokemonService;
import com.poke.api.util.ApiError;
import com.poke.api.util.ApiMessages;
import com.poke.api.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // Injects the Pokemon service.
    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    // Returns a paginated Pokemon list.
    @GetMapping
    @TokenRequired
    @Operation(summary = "List Pokemon with pagination", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PokemonPageResponse>> getPokemonPage(
            @RequestParam(defaultValue = "20") @Min(1) int limit,
            @RequestParam(defaultValue = "0") @Min(0) int offset) {
        try {
            PokemonPageResponse data = pokemonService.getPokemonPage(limit, offset);
            return ResponseEntity.ok(new ApiResponse<>(data));
        } catch (ResourceAccessException ex) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(new ApiResponse<>(ApiMessages.ERROR_EXTERNAL_SERVICE_TIMEOUT.getMessage(),
                            List.of(ApiError.ErrorCodes.EXTERNAL_SERVICE_TIMEOUT), HttpStatus.GATEWAY_TIMEOUT));
        } catch (RestClientException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new ApiResponse<>(ApiMessages.ERROR_EXTERNAL_SERVICE.getMessage(),
                            List.of(ApiError.ErrorCodes.EXTERNAL_SERVICE_ERROR), HttpStatus.BAD_GATEWAY));
        }
    }

    // Returns the Pokemon detail by ID or name.
    @GetMapping("/{idOrName}")
    @TokenRequired
    @Operation(summary = "Get Pokemon detail by ID or name", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PokemonResponse>> getPokemonByIdOrName(@PathVariable String idOrName) {
        try {
            PokemonResponse data = pokemonService.getPokemonByIdOrName(idOrName);
            return ResponseEntity.ok(new ApiResponse<>(data));
        } catch (ResourceAccessException ex) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(new ApiResponse<>(ApiMessages.ERROR_EXTERNAL_SERVICE_TIMEOUT.getMessage(),
                            List.of(ApiError.ErrorCodes.EXTERNAL_SERVICE_TIMEOUT), HttpStatus.GATEWAY_TIMEOUT));
        } catch (RestClientException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new ApiResponse<>(ApiMessages.ERROR_EXTERNAL_SERVICE.getMessage(),
                            List.of(ApiError.ErrorCodes.EXTERNAL_SERVICE_ERROR), HttpStatus.BAD_GATEWAY));
        }
    }
}
