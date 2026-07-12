package com.poke.api.service;

import com.poke.api.dto.external.PokeApiNamedResource;
import com.poke.api.dto.external.PokeApiPokemonDto;
import com.poke.api.dto.response.PokemonPageItemResponse;
import com.poke.api.dto.response.PokemonPageResponse;
import com.poke.api.dto.response.PokemonResponse;
import com.poke.api.proxy.client.PokeApiClient;
import com.poke.api.model.Pokemon;
import com.poke.api.repository.PokemonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.net.URI;
import java.util.List;

@Service
public class PokemonService {

    private final PokeApiClient pokeApiClient;
    private final PokemonRepository pokemonRepository;
    private final ObjectMapper objectMapper;

    // Injects the PokeAPI client for page queries.
    public PokemonService(@Qualifier("pokeApiRestClient") PokeApiClient pokeApiClient,
                          PokemonRepository pokemonRepository,
                          ObjectMapper objectMapper) {
        this.pokeApiClient = pokeApiClient;
        this.pokemonRepository = pokemonRepository;
        this.objectMapper = objectMapper;
    }

    // Converts the external response into the public contract.
    public PokemonPageResponse getPokemonPage(int limit, int offset) {
        PokeApiPokemonDto dto = pokeApiClient.getPokemonPage(limit, offset);
        List<PokemonPageItemResponse> results = dto == null || dto.getResults() == null
                ? List.of()
                : dto.getResults().stream().map(this::toItem).toList();

        int totalElements = dto != null && dto.getCount() != null ? dto.getCount() : 0;
        int totalPages = limit > 0 ? (int) Math.ceil((double) totalElements / limit) : 0;
        int numberOfElements = results.size();

        return PokemonPageResponse.builder()
                .content(results)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .numberOfElements(numberOfElements)
                .size(limit)
                .build();
    }

    // Returns the Pokemon detail by id or name.
    @Transactional
    public PokemonResponse getPokemonByIdOrName(String idOrName) {
        Optional<PokemonResponse> cachedPokemon = findCachedPokemon(idOrName);
        if (cachedPokemon.isPresent()) {
            return cachedPokemon.get();
        }

        PokemonResponse response = pokeApiClient.getProductByIdOrName(idOrName);
        if (response != null) {
            pokemonRepository.save(cachePokemon(response));
        }
        return response;
    }

    // Maps a basic PokeAPI resource into the internal response.
    private PokemonPageItemResponse toItem(PokeApiNamedResource resource) {
        return PokemonPageItemResponse.builder()
                .name(resource.getName())
                .id(extractId(resource.getUrl()))
                .build();
    }

    // Extracts the numeric identifier from the URL.
    private Integer extractId(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        String path = URI.create(url).getPath();
        String[] parts = path.split("/");
        for (int index = parts.length - 1; index >= 0; index--) {
            if (!parts[index].isBlank()) {
                try {
                    return Integer.valueOf(parts[index]);
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }

    // Looks up a cached Pokemon and rebuilds the public response.
    private Optional<PokemonResponse> findCachedPokemon(String idOrName) {
        Optional<Pokemon> cachedPokemon = resolveCachedPokemon(idOrName);
        if (cachedPokemon.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(cachedPokemon.get().getRawJson(), PokemonResponse.class));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    // Resolves the cached Pokemon by numeric id or name.
    private Optional<Pokemon> resolveCachedPokemon(String idOrName) {
        if (idOrName == null || idOrName.isBlank()) {
            return Optional.empty();
        }
        try {
            Long pokemonId = Long.valueOf(idOrName.trim());
            return pokemonRepository.findById(pokemonId);
        } catch (NumberFormatException ignored) {
            return pokemonRepository.findByNameIgnoreCase(idOrName.trim());
        }
    }

    // Converts the public response into a cache entity.
    private Pokemon cachePokemon(PokemonResponse response) {
        try {
            return Pokemon.builder()
                    .pokemonId(response.getId())
                    .name(response.getName())
                    .height(response.getHeight())
                    .weight(response.getWeight())
                    .baseExperience(response.getBaseExperience())
                    .imageUrl(response.getImageUrl())
                    .rawJson(objectMapper.writeValueAsString(response))
                    .active(true)
                    .createdBy("system")
                    .updatedBy("system")
                    .build();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to cache Pokemon detail", ex);
        }
    }
}