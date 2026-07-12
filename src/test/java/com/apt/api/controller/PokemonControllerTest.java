package com.poke.api.controller;

import com.poke.api.dto.response.PokemonPageResponse;
import com.poke.api.dto.response.PokemonResponse;
import com.poke.api.service.PokemonService;
import com.poke.api.util.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PokemonControllerTest {

    @Mock
    private PokemonService pokemonService;

    @InjectMocks
    private PokemonController pokemonController;

    @Test
    // Verifies the controller returns the paginated contract successfully.
    void getPokemonPage_Success() {
        when(pokemonService.getPokemonPage(anyInt(), anyInt())).thenReturn(new PokemonPageResponse());

        ResponseEntity<?> response = pokemonController.getPokemonPage(20, 20);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    // Verifies the controller returns the Pokemon detail contract successfully.
    void getPokemonByIdOrName_Success() {
        PokemonResponse pokemon = PokemonResponse.builder()
                .id(25L)
                .name("pikachu")
                .height(4)
                .weight(60)
                .baseExperience(112)
                .imageUrl("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png")
                .build();

        when(pokemonService.getPokemonByIdOrName("pikachu")).thenReturn(pokemon);

        ResponseEntity<?> response = pokemonController.getPokemonByIdOrName("pikachu");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertNotNull(apiResponse.getData());
    }
}
