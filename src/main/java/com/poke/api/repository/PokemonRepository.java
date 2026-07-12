package com.poke.api.repository;

import com.poke.api.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {

    // Finds a cached Pokemon by its name.
    Optional<Pokemon> findByNameIgnoreCase(String name);
}
