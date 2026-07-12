package com.poke.api.service;

import com.poke.api.dto.external.PokeApiNamedResource;
import com.poke.api.dto.external.PokeApiPokemonDto;
import com.poke.api.proxy.client.PokeApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PokemonServiceTest {

    @Mock
    private PokeApiClient pokeApiClient;

    @InjectMocks
    private PokemonService pokemonService;

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

        var response = pokemonService.getPokemonPage(20, 20);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(1351, response.getTotalElements());
        assertEquals(68, response.getTotalPages());
        assertEquals(1, response.getNumberOfElements());
        assertEquals(20, response.getSize());
    }
}
