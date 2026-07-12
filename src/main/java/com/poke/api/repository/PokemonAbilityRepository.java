package com.poke.api.repository;

import com.poke.api.model.PokemonAbility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PokemonAbilityRepository extends JpaRepository<PokemonAbility, Long> {

    // Finds an ability by its name ignoring case.
    Optional<PokemonAbility> findByNameIgnoreCase(String name);
}
