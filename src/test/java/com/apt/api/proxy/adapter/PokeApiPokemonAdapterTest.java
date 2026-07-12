package com.poke.api.proxy.adapter;

import com.poke.api.dto.external.PokeApiNamedResource;
import com.poke.api.dto.external.PokeApiPokemonDetailDto;
import com.poke.api.dto.response.PokemonResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PokeApiPokemonAdapterTest {

    private final PokeApiPokemonAdapter adapter = new PokeApiPokemonAdapter();

    @Test
    // Verifies the adapter maps the detail payload into the public response.
    void toPokemonResponse_MapsDetailFields() {
        PokeApiPokemonDetailDto dto = PokeApiPokemonDetailDto.builder()
                .id(25L)
                .name("pikachu")
                .height(4)
                .weight(60)
                .baseExperience(112)
                .sprites(PokeApiPokemonDetailDto.Sprites.builder()
                        .frontDefault("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png")
                        .build())
                .types(List.of(PokeApiPokemonDetailDto.PokemonTypeSlot.builder()
                        .slot(1)
                        .type(PokeApiNamedResource.builder().name("electric").url("https://pokeapi.co/api/v2/type/13/").build())
                        .build()))
                .abilities(List.of(
                        PokeApiPokemonDetailDto.PokemonAbilitySlot.builder()
                                .slot(1)
                                .isHidden(false)
                                .ability(PokeApiNamedResource.builder().name("static").url("https://pokeapi.co/api/v2/ability/9/").build())
                                .build(),
                        PokeApiPokemonDetailDto.PokemonAbilitySlot.builder()
                                .slot(3)
                                .isHidden(true)
                                .ability(PokeApiNamedResource.builder().name("lightning-rod").url("https://pokeapi.co/api/v2/ability/31/").build())
                                .build()))
                .stats(List.of(
                        PokeApiPokemonDetailDto.PokemonStat.builder()
                                .baseStat(35)
                                .effort(0)
                                .stat(PokeApiNamedResource.builder().name("hp").url("https://pokeapi.co/api/v2/stat/1/").build())
                                .build(),
                        PokeApiPokemonDetailDto.PokemonStat.builder()
                                .baseStat(55)
                                .effort(0)
                                .stat(PokeApiNamedResource.builder().name("attack").url("https://pokeapi.co/api/v2/stat/2/").build())
                                .build(),
                        PokeApiPokemonDetailDto.PokemonStat.builder()
                                .baseStat(40)
                                .effort(0)
                                .stat(PokeApiNamedResource.builder().name("defense").url("https://pokeapi.co/api/v2/stat/3/").build())
                                .build(),
                        PokeApiPokemonDetailDto.PokemonStat.builder()
                                .baseStat(50)
                                .effort(0)
                                .stat(PokeApiNamedResource.builder().name("special-attack").url("https://pokeapi.co/api/v2/stat/4/").build())
                                .build(),
                        PokeApiPokemonDetailDto.PokemonStat.builder()
                                .baseStat(50)
                                .effort(0)
                                .stat(PokeApiNamedResource.builder().name("special-defense").url("https://pokeapi.co/api/v2/stat/5/").build())
                                .build(),
                        PokeApiPokemonDetailDto.PokemonStat.builder()
                                .baseStat(90)
                                .effort(2)
                                .stat(PokeApiNamedResource.builder().name("speed").url("https://pokeapi.co/api/v2/stat/6/").build())
                                .build()))
                .build();

        PokemonResponse response = adapter.toPokemonResponse(dto);

        assertNotNull(response);
        assertEquals(25L, response.getId());
        assertEquals("pikachu", response.getName());
        assertEquals(4, response.getHeight());
        assertEquals(60, response.getWeight());
        assertEquals(112, response.getBaseExperience());
        assertEquals("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png", response.getImageUrl());
        assertEquals(List.of("electric"), response.getTypes());
        assertEquals("static", response.getAbilities().get(0).getName());
        assertEquals(Boolean.FALSE, response.getAbilities().get(0).getIsHidden());
        assertEquals(1, response.getAbilities().get(0).getSlot());
        assertEquals("lightning-rod", response.getAbilities().get(1).getName());
        assertEquals(Boolean.TRUE, response.getAbilities().get(1).getIsHidden());
        assertEquals(3, response.getAbilities().get(1).getSlot());
        assertEquals("hp", response.getStats().get(0).getName());
        assertEquals(35, response.getStats().get(0).getBaseStat());
        assertEquals(0, response.getStats().get(0).getEffort());
        assertEquals("speed", response.getStats().get(5).getName());
        assertEquals(90, response.getStats().get(5).getBaseStat());
        assertEquals(2, response.getStats().get(5).getEffort());
    }
}
