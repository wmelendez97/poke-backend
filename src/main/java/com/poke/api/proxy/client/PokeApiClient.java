package com.poke.api.proxy.client;

import com.poke.api.dto.external.*;
import com.poke.api.dto.response.*;
import com.poke.api.proxy.adapter.PokeApiPokemonAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("pokeApiRestClient")
public class PokeApiClient {

    private final RestTemplate pokeApiRestTemplate;
    private final PokeApiPokemonAdapter adapter;

    // Constructor for PokeApiClient, injecting RestTemplate and PokeApiPokemonAdapter.
    public PokeApiClient(@Qualifier("pokeApiRestTemplate") RestTemplate pokeApiRestTemplate,
                           PokeApiPokemonAdapter adapter) {
        this.pokeApiRestTemplate = pokeApiRestTemplate;
        this.adapter = adapter;
    }

    // Retrieves a list of all pokemons from the external API.
    // Removed @Override
    public List<PokemonResponse> getAllPokemons() {
        PokeApiPokemonDto dto = pokeApiRestTemplate.getForObject("/pokemon?limit=2000", PokeApiPokemonDto.class);
        if (dto == null || dto.getResults() == null) {
            return Collections.emptyList();
        }
        return adapter.toPokemonResponseList(dto.getResults());
    }

    // Retrieves a product (Pokemon) by its ID or name from the external API.
    // Removed @Override
    public PokemonResponse getProductByIdOrName(String idOrName) {
        PokeApiPokemonDetailDto dto = pokeApiRestTemplate.getForObject("/pokemon/{idOrName}", PokeApiPokemonDetailDto.class, idOrName);
        return adapter.toPokemonResponse(dto);
    }

    // Retrieves a paginated list of pokemons from the external API.
    // Removed @Override
    public List<PokemonResponse> getPokemonsPaged(int page, int pageSize) {
        int offset = Math.max(page, 0) * Math.max(pageSize, 0);
        PokeApiPokemonDto dto = pokeApiRestTemplate.getForObject("/pokemon?limit=" + pageSize + "&offset=" + offset, PokeApiPokemonDto.class);
        if (dto == null || dto.getResults() == null) {
            return Collections.emptyList();
        }
        return adapter.toPokemonResponseList(dto.getResults().stream().filter(Objects::nonNull).toList());
    }

    public PokeApiPokemonDto getPokemonPage(int limit, int offset) {
        return pokeApiRestTemplate.getForObject("/pokemon?limit=" + limit + "&offset=" + offset, PokeApiPokemonDto.class);
    }

    // Retrieves a list of all types from the external API.
    // Removed @Override
    public List<TypeResponse> getAllTypes() {
        PokeApiTypeDto dto = pokeApiRestTemplate.getForObject("/type", PokeApiTypeDto.class);
        if (dto == null || dto.getResults() == null) {
            return Collections.emptyList();
        }
        return adapter.toTypeResponseList(dto.getResults());
    }

    // Retrieves a type by its name from the external API.
    // Removed @Override
    public TypeResponse getTypeByName(String name) {
        PokeApiTypeDetailDto dto = pokeApiRestTemplate.getForObject("/type/{name}", PokeApiTypeDetailDto.class, name);
        return adapter.toTypeResponse(dto);
    }

    // Retrieves a list of abilities for a specific Pokemon from the external API.
    // Removed @Override
    public List<AbilityResponse> getPokemonAbilities(String idOrName) {
        PokeApiPokemonDetailDto dto = pokeApiRestTemplate.getForObject("/pokemon/{idOrName}", PokeApiPokemonDetailDto.class, idOrName);
        return Optional.ofNullable(dto)
                .map(PokeApiPokemonDetailDto::getAbilities)
                .orElse(Collections.emptyList())
                .stream()
                .map(adapter::toAbilityResponse)
                .collect(Collectors.toList());
    }

    // Retrieves a list of moves for a specific Pokemon from the external API.
    // Removed @Override
    public List<MoveResponse> getPokemonMoves(String idOrName) {
        PokeApiPokemonDetailDto dto = pokeApiRestTemplate.getForObject("/pokemon/{idOrName}", PokeApiPokemonDetailDto.class, idOrName);
        return Optional.ofNullable(dto)
                .map(PokeApiPokemonDetailDto::getMoves)
                .orElse(Collections.emptyList())
                .stream()
                .map(moveSlot -> adapter.toMoveResponse((PokeApiNamedResource) moveSlot.getMove()))
                .collect(Collectors.toList());
    }
}
