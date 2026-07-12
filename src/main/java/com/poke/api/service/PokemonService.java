package com.poke.api.service;

import com.poke.api.dto.external.PokeApiNamedResource;
import com.poke.api.dto.external.PokeApiPokemonDetailDto;
import com.poke.api.dto.external.PokeApiPokemonDto;
import com.poke.api.dto.response.AbilityResponse;
import com.poke.api.dto.response.MoveResponse;
import com.poke.api.dto.response.PokemonPageItemResponse;
import com.poke.api.dto.response.PokemonPageResponse;
import com.poke.api.dto.response.PokemonResponse;
import com.poke.api.model.PokemonAbility;
import com.poke.api.model.PokemonAbilityRel;
import com.poke.api.model.PokemonMove;
import com.poke.api.model.PokemonMoveRel;
import com.poke.api.model.Pokemon;
import com.poke.api.proxy.client.PokeApiClient;
import com.poke.api.repository.PokemonRepository;
import com.poke.api.repository.PokemonAbilityRelRepository;
import com.poke.api.repository.PokemonAbilityRepository;
import com.poke.api.repository.PokemonMoveRelRepository;
import com.poke.api.repository.PokemonMoveRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Optional;
import java.net.URI;
import java.util.List;

@Service
public class PokemonService {

    private static final Logger log = LoggerFactory.getLogger(PokemonService.class);

    private final PokeApiClient pokeApiClient;
    private final PokemonRepository pokemonRepository;
    private final PokemonAbilityRepository pokemonAbilityRepository;
    private final PokemonAbilityRelRepository pokemonAbilityRelRepository;
    private final PokemonMoveRepository pokemonMoveRepository;
    private final PokemonMoveRelRepository pokemonMoveRelRepository;
    private final ObjectMapper objectMapper;

    // Injects the PokeAPI client for page queries.
    public PokemonService(@Qualifier("pokeApiRestClient") PokeApiClient pokeApiClient,
                          PokemonRepository pokemonRepository,
                          PokemonAbilityRepository pokemonAbilityRepository,
                          PokemonAbilityRelRepository pokemonAbilityRelRepository,
                          PokemonMoveRepository pokemonMoveRepository,
                          PokemonMoveRelRepository pokemonMoveRelRepository,
                          ObjectMapper objectMapper) {
        this.pokeApiClient = pokeApiClient;
        this.pokemonRepository = pokemonRepository;
        this.pokemonAbilityRepository = pokemonAbilityRepository;
        this.pokemonAbilityRelRepository = pokemonAbilityRelRepository;
        this.pokemonMoveRepository = pokemonMoveRepository;
        this.pokemonMoveRelRepository = pokemonMoveRelRepository;
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
    public PokemonResponse getPokemonByIdOrName(String idOrName, String username) {
        Optional<PokemonResponse> cachedPokemon = findCachedPokemon(idOrName);
        if (cachedPokemon.isPresent() && isDetailComplete(cachedPokemon.get())) {
            return cachedPokemon.get();
        }

        PokemonResponse response = pokeApiClient.getProductByIdOrName(idOrName);
        if (response != null) {
            pokemonRepository.save(cachePokemon(response, username));
        }
        return response;
    }

    // Searches Pokemon by exact or partial name using cached data first.
    @Transactional(readOnly = true)
    public List<PokemonResponse> searchPokemonByName(String name, String username) {
        if (name == null || name.isBlank()) {
            return List.of();
        }

        Optional<PokemonResponse> exactMatch = findCachedPokemon(name);
        if (exactMatch.isPresent()) {
            return List.of(exactMatch.get());
        }

        List<Pokemon> cachedPokemon = pokemonRepository.findByNameContainingIgnoreCase(name.trim());
        if (!cachedPokemon.isEmpty()) {
            return cachedPokemon.stream()
                    .map(this::toResponse)
                    .flatMap(Optional::stream)
                    .toList();
        }

        PokemonResponse response = pokeApiClient.getProductByIdOrName(name.trim());
        if (response == null) {
            return List.of();
        }

        pokemonRepository.save(cachePokemon(response, username));
        return List.of(response);
    }

    // Returns the abilities of a Pokemon and persists the catalog and relationship.
    @Transactional
    public List<AbilityResponse> getPokemonAbilities(String idOrName, String username) {
        PokeApiPokemonDetailDto detail = pokeApiClient.getPokemonDetail(idOrName);
        if (detail == null) {
            return List.of();
        }
        PokemonResponse response = pokeApiClient.getProductByIdOrName(idOrName);
        if (response != null) {
            pokemonRepository.save(cachePokemon(response, username));
        }
        return persistAbilities(detail);
    }

    // Returns the moves of a Pokemon and persists the catalog and relationship.
    @Transactional
    public List<MoveResponse> getPokemonMoves(String idOrName, String username) {
        PokeApiPokemonDetailDto detail = pokeApiClient.getPokemonDetail(idOrName);
        if (detail == null || detail.getMoves() == null || detail.getMoves().isEmpty()) {
            return List.of();
        }
        PokemonResponse response = pokeApiClient.getProductByIdOrName(idOrName);
        if (response != null) {
            pokemonRepository.save(cachePokemon(response, username));
        }
        return persistMoves(detail);
    }

    // Maps a basic PokeAPI resource into the internal response.
    private PokemonPageItemResponse toItem(PokeApiNamedResource resource) {
        return PokemonPageItemResponse.builder()
                .name(resource.getName())
                .id(extractId(resource.getUrl()))
                .build();
    }

    // Checks whether the cached Pokemon contains the full detail payload.
    private boolean isDetailComplete(PokemonResponse response) {
        return response != null
                && response.getHeight() != null
                && response.getWeight() != null
                && response.getBaseExperience() != null
                && response.getImageUrl() != null
                && response.getTypes() != null
                && response.getAbilities() != null
                && response.getStats() != null;
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
    private Pokemon cachePokemon(PokemonResponse response, String username) {
        try {
            String rawJson = objectMapper.writeValueAsString(response);
            log.info("Guardando raw_json de pokemon id={}, raw_json={}", response.getId(), rawJson);
            return Pokemon.builder()
                    .pokemonId(response.getId())
                    .name(response.getName())
                    .height(response.getHeight())
                    .weight(response.getWeight())
                    .baseExperience(response.getBaseExperience())
                    .imageUrl(response.getImageUrl())
                    .rawJson(rawJson)
                    .active(true)
                    .createdBy(username)
                    .updatedBy(username)
                    .build();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to cache Pokemon detail", ex);
        }
    }

    // Rebuilds a public response from a cached Pokemon entity.
    private Optional<PokemonResponse> toResponse(Pokemon pokemon) {
        try {
            return Optional.of(objectMapper.readValue(pokemon.getRawJson(), PokemonResponse.class));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    // Persists ability catalog and relation for a Pokemon.
    private List<AbilityResponse> persistAbilities(PokeApiPokemonDetailDto detail) {
        if (detail.getAbilities() == null) {
            return List.of();
        }
        return detail.getAbilities().stream()
                .map(slot -> {
                    if (slot == null || slot.getAbility() == null) {
                        return null;
                    }
                    Long pokemonId = detail.getId();
                    Long abilityId = Long.valueOf(extractId(slot.getAbility().getUrl()));
                    if (abilityId != null) {
                        PokemonAbility ability = pokemonAbilityRepository.findById(abilityId)
                                .orElseGet(() -> pokemonAbilityRepository.save(PokemonAbility.builder()
                                        .id(abilityId)
                                        .name(slot.getAbility().getName())
                                        .rawJson(writeJson(slot.getAbility()))
                                        .build()));
                        log.info("Guardando raw_json de ability id={}, raw_json={}", ability.getId(), ability.getRawJson());
                        pokemonAbilityRelRepository.save(PokemonAbilityRel.builder()
                                .id(PokemonAbilityRel.PokemonAbilityRelId.builder()
                                        .pokemonId(pokemonId)
                                        .abilityId(ability.getId())
                                        .build())
                                .isHidden(slot.getIsHidden())
                                .slot(slot.getSlot())
                                .build());
                    }
                    return AbilityResponse.builder()
                            .id(abilityId)
                            .name(slot.getAbility().getName())
                            .isHidden(slot.getIsHidden())
                            .slot(slot.getSlot())
                            .build();
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    // Persists move catalog and relation for a Pokemon.
    private List<MoveResponse> persistMoves(PokeApiPokemonDetailDto detail) {
        return detail.getMoves().stream()
                .map(slot -> {
                    if (slot == null || slot.getMove() == null) {
                        return null;
                    }
                    Long moveId = Long.valueOf(extractId(slot.getMove().getUrl()));
                    if (moveId != null) {
                        PokemonMove move = pokemonMoveRepository.findById(moveId)
                                .orElseGet(() -> pokemonMoveRepository.save(PokemonMove.builder()
                                        .id(moveId)
                                        .name(slot.getMove().getName())
                                        .rawJson(writeJson(slot.getMove()))
                                        .build()));
                        log.info("Guardando raw_json de move id={}, raw_json={}", move.getId(), move.getRawJson());
                        pokemonMoveRelRepository.save(PokemonMoveRel.builder()
                                .id(PokemonMoveRel.PokemonMoveRelId.builder()
                                        .pokemonId(detail.getId())
                                        .moveId(move.getId())
                                        .build())
                                .build());
                    }
                    return MoveResponse.builder()
                            .id(moveId)
                            .name(slot.getMove().getName())
                            .build();
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    // Serializes any object to JSON for catalog backup.
    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to cache Pokemon relation payload", ex);
        }
    }
}
