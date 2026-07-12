package com.apt.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poke.api.dto.response.PokemonResponse;
import com.poke.api.dto.response.TypeResponse;
import com.poke.api.dto.response.TypeSummaryResponse;
import com.poke.api.model.Pokemon;
import com.poke.api.repository.PokemonRepository;
import com.poke.api.repository.PokemonTypeRelRepository;
import com.poke.api.repository.PokemonTypeRepository;
import com.poke.api.service.TypeService;
import com.poke.api.proxy.client.PokeApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypeServiceTest {

    @Mock
    private PokeApiClient pokeApiClient;

    @Mock
    private PokemonTypeRepository pokemonTypeRepository;

    @Mock
    private PokemonTypeRelRepository pokemonTypeRelRepository;

    @Mock
    private PokemonRepository pokemonRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Builds the service with mocked dependencies.
    private TypeService createService() {
        return new TypeService(pokeApiClient, pokemonTypeRepository, pokemonTypeRelRepository, pokemonRepository, objectMapper);
    }

    @Test
    // Verifies the service returns the type catalog.
    void getAllTypes_ReturnsTypes() {
        when(pokeApiClient.getAllTypes()).thenReturn(List.of(TypeResponse.builder().id(13L).name("electric").build()));

        var response = createService().getAllTypes("testuser");

        assertEquals(1, response.size());
        assertEquals("electric", response.get(0).getName());
    }

    @Test
    // Verifies the service returns Pokemon by type.
    void getPokemonByType_ReturnsPokemon() throws Exception {
        TypeResponse typeResponse = TypeResponse.builder()
                .id(13L)
                .name("electric")
                .pokemon(List.of(PokemonResponse.builder().id(25L).name("pikachu").build()))
                .build();

        when(pokeApiClient.getTypeByName("electric")).thenReturn(typeResponse);
        when(pokemonTypeRepository.findByNameIgnoreCase("electric")).thenReturn(java.util.Optional.empty());
        when(pokemonTypeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(pokemonRepository.findById(25L)).thenReturn(java.util.Optional.of(Pokemon.builder().pokemonId(25L).name("pikachu").rawJson(objectMapper.writeValueAsString(PokemonResponse.builder().id(25L).name("pikachu").build())).build()));
        when(pokemonTypeRelRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = createService().getPokemonByType("electric", "testuser");

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("pikachu", response.get(0).getName());
    }
}
