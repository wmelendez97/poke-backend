package com.poke.api.repository;

import com.poke.api.model.PokemonMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PokemonMoveRepository extends JpaRepository<PokemonMove, Long> {

    // Finds a move by its name ignoring case.
    Optional<PokemonMove> findByNameIgnoreCase(String name);
}
