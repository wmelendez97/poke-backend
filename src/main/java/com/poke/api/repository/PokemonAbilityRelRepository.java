package com.poke.api.repository;

import com.poke.api.model.PokemonAbilityRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonAbilityRelRepository extends JpaRepository<PokemonAbilityRel, PokemonAbilityRel.PokemonAbilityRelId> {
}
