package com.poke.api.proxy.adapter;

import com.poke.api.dto.external.*;
import com.poke.api.dto.response.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// Adapter class to convert external PokeAPI DTOs to internal Response DTOs.
@Component
public class PokeApiPokemonAdapter {

    private static final Pattern TRAILING_ID_PATTERN = Pattern.compile(".*/(\\d+)/?$");

    // Converts a PokeApiPokemonDetailDto to a PokemonResponse.
    public PokemonResponse toPokemonResponse(PokeApiPokemonDetailDto dto) {
        if (dto == null) {
            return null;
        }
        return PokemonResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .height(dto.getHeight())
                .weight(dto.getWeight())
                .baseExperience(dto.getBaseExperience())
                .imageUrl(Optional.ofNullable(dto.getSprites()).map(PokeApiPokemonDetailDto.Sprites::getFrontDefault).orElse(null))
                .types(Optional.ofNullable(dto.getTypes()).orElse(List.of()).stream()
                        .map(slot -> slot.getType().getName())
                        .collect(Collectors.toList()))
                .abilities(Optional.ofNullable(dto.getAbilities()).orElse(List.of()).stream()
                        .map(this::toAbilityResponse)
                        .collect(Collectors.toList()))
                .stats(Optional.ofNullable(dto.getStats()).orElse(List.of()).stream()
                        .map(this::toStatResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    // Converts a PokeApiNamedResource to a basic PokemonResponse (for lists).
    public PokemonResponse toPokemonResponse(PokeApiNamedResource resource) {
        if (resource == null) {
            return null;
        }
        return PokemonResponse.builder()
                .id(extractIdFromUrl(resource.getUrl()))
                .name(resource.getName())
                .url(resource.getUrl())
                .build();
    }

    // Converts a list of PokeApiNamedResource to a list of PokemonResponse.
    public List<PokemonResponse> toPokemonResponseList(List<PokeApiNamedResource> resources) {
        return resources.stream()
                .filter(Objects::nonNull)
                .map(this::toPokemonResponse)
                .collect(Collectors.toList());
    }

    // Converts a PokeApiTypeDetailDto to a TypeResponse.
    public TypeResponse toTypeResponse(PokeApiTypeDetailDto dto) {
        if (dto == null) {
            return null;
        }
        return TypeResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .pokemon(Optional.ofNullable(dto.getPokemon()).orElse(List.of()).stream()
                        .map(slot -> toPokemonResponse(slot.getPokemon()))
                        .collect(Collectors.toList()))
                .build();
    }

    // Converts a list of PokeApiNamedResource to a list of TypeResponse.
    public List<TypeResponse> toTypeResponseList(List<PokeApiNamedResource> resources) {
        return resources.stream()
                .filter(Objects::nonNull)
                .map(resource -> TypeResponse.builder()
                        .id(extractIdFromUrl(resource.getUrl()))
                        .name(resource.getName())
                        .build())
                .collect(Collectors.toList());
    }

    // Converts a list of named resources into the compact type catalog response.
    public List<TypeSummaryResponse> toTypeSummaryResponseList(List<PokeApiNamedResource> resources) {
        return resources.stream()
                .filter(Objects::nonNull)
                .map(resource -> TypeSummaryResponse.builder()
                        .id(extractIdFromUrl(resource.getUrl()))
                        .name(resource.getName())
                        .build())
                .collect(Collectors.toList());
    }

    // Converts a PokeApiAbilityDto to an AbilityResponse.
    public AbilityResponse toAbilityResponse(PokeApiAbilityDto dto) {
        if (dto == null) {
            return null;
        }
        return AbilityResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    // Converts a PokeApiPokemonDetailDto.PokemonAbilitySlot to an AbilityResponse.
    public AbilityResponse toAbilityResponse(PokeApiPokemonDetailDto.PokemonAbilitySlot slot) {
        if (slot == null) {
            return null;
        }
        return AbilityResponse.builder()
                .name(slot.getAbility().getName())
                .isHidden(slot.getIsHidden())
                .slot(slot.getSlot())
                .build();
    }

    // Converts a PokeApiMoveDto to a MoveResponse.
    public MoveResponse toMoveDetailResponse(PokeApiMoveDto dto) {
        if (dto == null) {
            return null;
        }
        return MoveResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    // Converts a PokeApiNamedResource (representing a move) to a MoveResponse.
    public MoveResponse toMoveResponse(PokeApiNamedResource resource) {
        if (resource == null) {
            return null;
        }
        return MoveResponse.builder()
                .id(extractIdFromUrl(resource.getUrl()))
                .name(resource.getName())
                .build();
    }

    // Converts a PokeApiPokemonDetailDto.PokemonStat to a StatResponse.
    public StatResponse toStatResponse(PokeApiPokemonDetailDto.PokemonStat stat) {
        if (stat == null) {
            return null;
        }
        return StatResponse.builder()
                .name(stat.getStat().getName())
                .baseStat(stat.getBaseStat())
                .effort(stat.getEffort())
                .build();
    }

    // Extracts the ID from a given URL.
    private Long extractIdFromUrl(String url) {
        if (url == null) {
            return null;
        }
        Matcher matcher = TRAILING_ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }
}
