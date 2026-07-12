package com.poke.api.service;

import com.poke.api.model.SearchHistory;
import com.poke.api.repository.SearchHistoryRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Service
public class SearchHistoryService {

    private static final Logger log = LoggerFactory.getLogger(SearchHistoryService.class);

    private final SearchHistoryRepository searchHistoryRepository;

    // Injects the search history repository.
    public SearchHistoryService(SearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    // Saves a search history record.
    public void saveSearchHistory(String userIdentifier, String searchType, String queryValue, String endpoint,
                                  Boolean success, Integer statusCode, String errorMessage) {
        log.info("Persisting search_history user_identifier={}, search_type={}, query_value={}, endpoint={}, success={}, status_code={}, error_message={}",
                userIdentifier, searchType, queryValue, endpoint, success, statusCode, errorMessage);
        SearchHistory searchHistory = SearchHistory.builder()
                .userIdentifier(userIdentifier)
                .searchType(searchType)
                .queryValue(queryValue)
                .endpoint(endpoint)
                .success(success)
                .statusCode(statusCode)
                .errorMessage(errorMessage)
                .createdAt(LocalDateTime.now())
                .build();
        searchHistoryRepository.save(searchHistory);
    }
}
