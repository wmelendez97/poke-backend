package com.poke.api.repository;

import com.poke.api.model.PokemonMoveRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonMoveRelRepository extends JpaRepository<PokemonMoveRel, PokemonMoveRel.PokemonMoveRelId> {
}
