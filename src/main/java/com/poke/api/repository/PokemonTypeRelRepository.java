package com.poke.api.repository;

import com.poke.api.model.PokemonTypeRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonTypeRelRepository extends JpaRepository<PokemonTypeRel, PokemonTypeRel.PokemonTypeRelId> {
}
