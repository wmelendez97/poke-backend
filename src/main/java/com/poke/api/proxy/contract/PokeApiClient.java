package com.poke.api.proxy.contract;

import com.poke.api.dto.response.AbilityResponse;
import com.poke.api.dto.response.MoveResponse;
import com.poke.api.dto.response.PokemonResponse;
import com.poke.api.dto.response.TypeResponse;

import java.util.List;

public interface PokeApiClient {

    // Retrieves a list of all pokemons from the external API.
    List<PokemonResponse> getAllPokemons();

    // Retrieves a product (Pokemon) by its ID or name from the external API.
    PokemonResponse getProductByIdOrName(String idOrName);

    // Retrieves a paginated list of pokemons from the external API.
    List<PokemonResponse> getPokemonsPaged(int page, int pageSize);

    // Retrieves a list of all types from the external API.
    List<TypeResponse> getAllTypes();

    // Retrieves a type by its name from the external API.
    TypeResponse getTypeByName(String name);

    // Retrieves a list of abilities for a specific Pokemon from the external API.
    List<AbilityResponse> getPokemonAbilities(String idOrName);

    // Retrieves a list of moves for a specific Pokemon from the external API.
    List<MoveResponse> getPokemonMoves(String idOrName);
}