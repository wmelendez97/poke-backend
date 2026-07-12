package com.poke.api.repository;

import com.poke.api.model.PokemonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PokemonTypeRepository extends JpaRepository<PokemonType, Long> {

    // Finds a type by its name ignoring case.
    Optional<PokemonType> findByNameIgnoreCase(String name);
}
