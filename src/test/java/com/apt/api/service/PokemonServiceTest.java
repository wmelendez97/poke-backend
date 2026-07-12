package com.poke.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poke.api.dto.external.PokeApiNamedResource;
import com.poke.api.dto.external.PokeApiPokemonDto;
import com.poke.api.dto.response.PokemonResponse;
import com.poke.api.model.Pokemon;
import com.poke.api.proxy.client.PokeApiClient;
import com.poke.api.repository.PokemonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PokemonServiceTest {

    @Mock
    private PokeApiClient pokeApiClient;

    @Mock
    private PokemonRepository pokemonRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Builds the service with mocked dependencies.
    private PokemonService createService() {
        return new PokemonService(pokeApiClient, pokemonRepository, objectMapper);
    }

    @Test
    // Verifies pagination metadata and minimal mapped content.
    void getPokemonPage_BuildsPageLikeResponse() {
        PokeApiNamedResource resource = new PokeApiNamedResource();
        resource.setName("bulbasaur");
        resource.setUrl("https://pokeapi.co/api/v2/pokemon/1/");

        PokeApiPokemonDto dto = new PokeApiPokemonDto();
        dto.setCount(1351);
        dto.setNext("https://pokeapi.co/api/v2/pokemon/?offset=20&limit=20");
        dto.setPrevious(null);
        dto.setResults(List.of(resource));

        when(pokeApiClient.getPokemonPage(20, 20)).thenReturn(dto);

        var response = createService().getPokemonPage(20, 20);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(1351, response.getTotalElements());
        assertEquals(68, response.getTotalPages());
        assertEquals(1, response.getNumberOfElements());
        assertEquals(20, response.getSize());
    }

    @Test
    // Verifies the service returns the cached Pokemon detail without calling PokeAPI.
    void getPokemonByIdOrName_ReturnsCachedDetail() throws Exception {
        PokemonResponse cachedResponse = PokemonResponse.builder()
                .id(25L)
                .name("pikachu")
                .height(4)
                .weight(60)
                .baseExperience(112)
                .imageUrl("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png")
                .types(List.of("electric"))
                .build();

        Pokemon cachedPokemon = Pokemon.builder()
                .pokemonId(25L)
                .name("pikachu")
                .height(4)
                .weight(60)
                .baseExperience(112)
                .imageUrl("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png")
                .rawJson(objectMapper.writeValueAsString(cachedResponse))
                .build();

        when(pokemonRepository.findById(25L)).thenReturn(Optional.of(cachedPokemon));

        var response = createService().getPokemonByIdOrName("25", "testuser");

        assertNotNull(response);
        assertEquals(25L, response.getId());
        assertEquals("pikachu", response.getName());
        assertEquals(List.of("electric"), response.getTypes());
        verify(pokeApiClient, never()).getProductByIdOrName(any());
    }

    @Test
    // Verifies the service fetches, caches, and returns the Pokemon detail.
    void getPokemonByIdOrName_FetchesAndCachesWhenMissing() throws Exception {
        PokemonResponse pokemon = PokemonResponse.builder()
                .id(25L)
                .name("pikachu")
                .height(4)
                .weight(60)
                .baseExperience(112)
                .imageUrl("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png")
                .build();

        when(pokeApiClient.getProductByIdOrName("pikachu")).thenReturn(pokemon);

        var response = createService().getPokemonByIdOrName("pikachu", "testuser");

        assertNotNull(response);
        assertEquals(25L, response.getId());
        assertEquals("pikachu", response.getName());
        assertEquals(4, response.getHeight());
        assertEquals(60, response.getWeight());
        assertEquals(112, response.getBaseExperience());
        verify(pokemonRepository).save(any(Pokemon.class));
    }
}
