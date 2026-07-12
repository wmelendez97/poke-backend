package com.poke.api.service;

import com.poke.api.dto.response.SearchHistoryResponse;
import com.poke.api.model.SearchHistory;
import com.poke.api.repository.SearchHistoryRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

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

    // Returns the global search history in descending creation order.
    public List<SearchHistoryResponse> getAllSearchHistory() {
        return searchHistoryRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    // Returns the search history for one user in descending creation order.
    public List<SearchHistoryResponse> getSearchHistoryByUser(String userIdentifier) {
        return searchHistoryRepository.findByUserIdentifierOrderByCreatedAtDesc(userIdentifier).stream()
                .map(this::toResponse)
                .toList();
    }

    // Maps a search history entity into its response DTO.
    private SearchHistoryResponse toResponse(SearchHistory searchHistory) {
        return SearchHistoryResponse.builder()
                .id(searchHistory.getId())
                .userIdentifier(searchHistory.getUserIdentifier())
                .searchType(searchHistory.getSearchType())
                .queryValue(searchHistory.getQueryValue())
                .endpoint(searchHistory.getEndpoint())
                .success(searchHistory.getSuccess())
                .statusCode(searchHistory.getStatusCode())
                .errorMessage(searchHistory.getErrorMessage())
                .createdAt(searchHistory.getCreatedAt())
                .build();
    }
}
