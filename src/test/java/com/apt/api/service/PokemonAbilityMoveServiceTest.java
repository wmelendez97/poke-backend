package com.apt.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poke.api.dto.external.PokeApiNamedResource;
import com.poke.api.dto.external.PokeApiPokemonDetailDto;
import com.poke.api.dto.response.AbilityResponse;
import com.poke.api.dto.response.PokemonResponse;
import com.poke.api.model.Pokemon;
import com.poke.api.service.PokemonService;
import com.poke.api.repository.PokemonAbilityRelRepository;
import com.poke.api.repository.PokemonAbilityRepository;
import com.poke.api.repository.PokemonMoveRelRepository;
import com.poke.api.repository.PokemonMoveRepository;
import com.poke.api.repository.PokemonRepository;
import com.poke.api.proxy.client.PokeApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PokemonAbilityMoveServiceTest {

    @Mock
    private PokeApiClient pokeApiClient;

    @Mock
    private PokemonRepository pokemonRepository;

    @Mock
    private PokemonAbilityRepository pokemonAbilityRepository;

    @Mock
    private PokemonAbilityRelRepository pokemonAbilityRelRepository;

    @Mock
    private PokemonMoveRepository pokemonMoveRepository;

    @Mock
    private PokemonMoveRelRepository pokemonMoveRelRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Builds the service with mocked dependencies.
    private PokemonService createService() {
        return new PokemonService(pokeApiClient, pokemonRepository, pokemonAbilityRepository, pokemonAbilityRelRepository, pokemonMoveRepository, pokemonMoveRelRepository, objectMapper);
    }

    @Test
    // Verifies abilities are returned from detail payload.
    void getPokemonAbilities_ReturnsAbilities() throws Exception {
        PokeApiNamedResource ability = new PokeApiNamedResource();
        ability.setName("static");
        ability.setUrl("https://pokeapi.co/api/v2/ability/9/");

        PokeApiPokemonDetailDto.PokemonAbilitySlot slot = new PokeApiPokemonDetailDto.PokemonAbilitySlot();
        slot.setAbility(ability);
        slot.setIsHidden(false);
        slot.setSlot(1);

        PokeApiPokemonDetailDto detail = PokeApiPokemonDetailDto.builder()
                .id(25L)
                .abilities(List.of(slot))
                .build();

        when(pokeApiClient.getPokemonDetail("pikachu")).thenReturn(detail);
        when(pokeApiClient.getProductByIdOrName("pikachu")).thenReturn(PokemonResponse.builder().id(25L).name("pikachu").build());
        when(pokemonAbilityRepository.findById(9L)).thenReturn(Optional.empty());
        when(pokemonAbilityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(pokemonAbilityRelRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = createService().getPokemonAbilities("pikachu", "testuser");

        assertEquals(1, response.size());
        assertEquals("static", response.get(0).getName());
    }

    @Test
    // Verifies moves are returned from detail payload.
    void getPokemonMoves_ReturnsMoves() {
        PokeApiNamedResource move = new PokeApiNamedResource();
        move.setName("mega-punch");
        move.setUrl("https://pokeapi.co/api/v2/move/5/");

        PokeApiPokemonDetailDto.PokemonMoveSlot slot = new PokeApiPokemonDetailDto.PokemonMoveSlot();
        slot.setMove(move);

        PokeApiPokemonDetailDto detail = PokeApiPokemonDetailDto.builder()
                .id(25L)
                .moves(List.of(slot))
                .build();

        when(pokeApiClient.getPokemonDetail("pikachu")).thenReturn(detail);
        when(pokeApiClient.getProductByIdOrName("pikachu")).thenReturn(PokemonResponse.builder().id(25L).name("pikachu").build());
        when(pokemonMoveRepository.findById(5L)).thenReturn(Optional.empty());
        when(pokemonMoveRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(pokemonMoveRelRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = createService().getPokemonMoves("pikachu", "testuser");

        assertEquals(1, response.size());
        assertEquals("mega-punch", response.get(0).getName());
    }
}
