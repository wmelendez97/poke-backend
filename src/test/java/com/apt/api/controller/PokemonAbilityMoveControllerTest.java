package com.apt.api.controller;

import com.poke.api.controller.PokemonController;
import com.poke.api.dto.response.AbilityResponse;
import com.poke.api.dto.response.MoveResponse;
import com.poke.api.service.PokemonService;
import com.poke.api.service.SearchHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PokemonAbilityMoveControllerTest {

    @Mock
    private PokemonService pokemonService;

    @Mock
    private SearchHistoryService searchHistoryService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private PokemonController pokemonController;

    @Test
    // Verifies the controller returns Pokemon abilities.
    void getPokemonAbilities_Success() {
        when(request.getAttribute("authenticatedUserId")).thenReturn(1L);
        when(request.getAttribute("authenticatedUserUsername")).thenReturn("testuser");
        when(request.getRequestURI()).thenReturn("/pokemon/pikachu/abilities");
        when(pokemonService.getPokemonAbilities("pikachu", "testuser")).thenReturn(List.of(AbilityResponse.builder().id(1L).name("static").build()));

        ResponseEntity<?> response = pokemonController.getPokemonAbilities("pikachu");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    // Verifies the controller returns Pokemon moves.
    void getPokemonMoves_Success() {
        when(request.getAttribute("authenticatedUserId")).thenReturn(1L);
        when(request.getAttribute("authenticatedUserUsername")).thenReturn("testuser");
        when(request.getRequestURI()).thenReturn("/pokemon/pikachu/moves");
        when(pokemonService.getPokemonMoves("pikachu", "testuser")).thenReturn(List.of(MoveResponse.builder().id(1L).name("mega-punch").build()));

        ResponseEntity<?> response = pokemonController.getPokemonMoves("pikachu");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
