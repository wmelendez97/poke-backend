package com.poke.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poke.api.dto.response.PokemonResponse;
import com.poke.api.dto.response.TypeResponse;
import com.poke.api.dto.response.TypeSummaryResponse;
import com.poke.api.model.Pokemon;
import com.poke.api.model.PokemonType;
import com.poke.api.model.PokemonTypeRel;
import com.poke.api.proxy.client.PokeApiClient;
import com.poke.api.repository.PokemonRepository;
import com.poke.api.repository.PokemonTypeRelRepository;
import com.poke.api.repository.PokemonTypeRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class TypeService {

    private static final Logger log = LoggerFactory.getLogger(TypeService.class);

    private final PokeApiClient pokeApiClient;
    private final PokemonTypeRepository pokemonTypeRepository;
    private final PokemonTypeRelRepository pokemonTypeRelRepository;
    private final PokemonRepository pokemonRepository;
    private final ObjectMapper objectMapper;

    // Injects the type and pokemon persistence dependencies.
    public TypeService(@Qualifier("pokeApiRestClient") PokeApiClient pokeApiClient,
                       PokemonTypeRepository pokemonTypeRepository,
                       PokemonTypeRelRepository pokemonTypeRelRepository,
                       PokemonRepository pokemonRepository,
                       ObjectMapper objectMapper) {
        this.pokeApiClient = pokeApiClient;
        this.pokemonTypeRepository = pokemonTypeRepository;
        this.pokemonTypeRelRepository = pokemonTypeRelRepository;
        this.pokemonRepository = pokemonRepository;
        this.objectMapper = objectMapper;
    }

    // Returns the type catalog and stores missing types locally.
    @Transactional
    public List<TypeSummaryResponse> getAllTypes(String username) {
        List<TypeResponse> types = pokeApiClient.getAllTypes();
        for (TypeResponse type : types) {
            if (type != null && type.getId() != null && type.getName() != null) {
                pokemonTypeRepository.save(pokemonTypeRepository.findById(type.getId())
                        .orElseGet(() -> PokemonType.builder().id(type.getId()).name(type.getName()).build()));
            }
        }
        return types.stream()
                .filter(type -> type != null)
                .map(type -> TypeSummaryResponse.builder()
                        .id(type.getId())
                        .name(type.getName())
                        .build())
                .toList();
    }

    // Returns Pokemon linked to a type, using local relation cache first.
    @Transactional
    public List<PokemonResponse> getPokemonByType(String name, String username) {
        Optional<PokemonType> cachedType = pokemonTypeRepository.findByNameIgnoreCase(name);
        TypeResponse typeResponse = pokeApiClient.getTypeByName(name);
        if (typeResponse == null) {
            return List.of();
        }

        PokemonType type = cachedType.orElseGet(() -> pokemonTypeRepository.save(
                PokemonType.builder()
                        .id(typeResponse.getId())
                        .name(typeResponse.getName())
                        .build()));

        if (typeResponse.getPokemon() == null || typeResponse.getPokemon().isEmpty()) {
            return List.of();
        }

        return typeResponse.getPokemon().stream()
                .filter(pokemon -> pokemon.getId() != null)
                .map(pokemon -> persistPokemonTypeRelation(pokemon, type, username))
                .map(this::toResponse)
                .flatMap(Optional::stream)
                .toList();
    }

    // Stores the pokemon-type relation and keeps the Pokemon cache in sync when possible.
    private com.poke.api.model.Pokemon persistPokemonTypeRelation(PokemonResponse response, PokemonType type, String username) {
        Pokemon pokemon = pokemonRepository.findById(response.getId())
                .orElseGet(() -> pokemonRepository.save(Pokemon.builder()
                        .pokemonId(response.getId())
                        .name(response.getName())
                        .rawJson(writeJson(response))
                        .active(true)
                        .createdBy(username)
                        .updatedBy(username)
                        .build()));

        PokemonTypeRel.PokemonTypeRelId relId = PokemonTypeRel.PokemonTypeRelId.builder()
                .pokemonId(pokemon.getPokemonId())
                .typeId(type.getId())
                .build();
        pokemonTypeRelRepository.save(PokemonTypeRel.builder().id(relId).build());
        return pokemon;
    }

    // Rebuilds a Pokemon response from a cached Pokemon entity.
    private Optional<PokemonResponse> toResponse(Pokemon pokemon) {
        try {
            return Optional.of(objectMapper.readValue(pokemon.getRawJson(), PokemonResponse.class));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    // Serializes a Pokemon response for cache storage.
    private String writeJson(PokemonResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to cache Pokemon type relation", ex);
        }
    }
}
