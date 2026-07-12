package com.poke.api.repository;

import com.poke.api.model.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findAllByOrderByCreatedAtDesc();

    List<SearchHistory> findByUserIdentifierOrderByCreatedAtDesc(String userIdentifier);
}
