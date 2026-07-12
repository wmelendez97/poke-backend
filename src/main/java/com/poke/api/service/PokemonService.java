package com.poke.api.service;

import com.poke.api.dto.external.PokeApiNamedResource;
import com.poke.api.dto.external.PokeApiPokemonDto;
import com.poke.api.dto.response.PokemonPageItemResponse;
import com.poke.api.dto.response.PokemonPageResponse;
import com.poke.api.dto.response.PokemonResponse;
import com.poke.api.proxy.client.PokeApiClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class PokemonService {

    private final PokeApiClient pokeApiClient;

    // Injects the PokeAPI client for page queries.
    public PokemonService(@Qualifier("pokeApiRestClient") PokeApiClient pokeApiClient) {
        this.pokeApiClient = pokeApiClient;
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
    public PokemonResponse getPokemonByIdOrName(String idOrName) {
        return pokeApiClient.getProductByIdOrName(idOrName);
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
}
