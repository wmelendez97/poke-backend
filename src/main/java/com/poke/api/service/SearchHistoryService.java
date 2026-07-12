package com.poke.api.service;

import com.poke.api.dto.response.SearchHistoryResponse;
import com.poke.api.model.SearchHistory;
import com.poke.api.model.User;
import com.poke.api.repository.SearchHistoryRepository;
import com.poke.api.repository.SearchHistorySpecifications;
import com.poke.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SearchHistoryService {

    private static final Logger log = LoggerFactory.getLogger(SearchHistoryService.class);

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;

    // Injects the search history repository.
    public SearchHistoryService(SearchHistoryRepository searchHistoryRepository, UserRepository userRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
        this.userRepository = userRepository;
    }

    // Saves a search history record.
    public void saveSearchHistory(Long userId, String userIdentifier, String searchType, String queryValue, String endpoint,
                                  Boolean success, Integer statusCode, String errorMessage) {
        log.info("Persisting search_history user_identifier={}, search_type={}, query_value={}, endpoint={}, success={}, status_code={}, error_message={}",
                userIdentifier, searchType, queryValue, endpoint, success, statusCode, errorMessage);
        User user = resolveUser(userId, userIdentifier).orElse(null);
        SearchHistory searchHistory = SearchHistory.builder()
                .userIdentifier(userIdentifier)
                .user(user)
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

    // Returns the global search history using optional filters.
    public List<SearchHistoryResponse> getAllSearchHistory(LocalDate fromDate, LocalDate toDate, String type, String text) {
        LocalDateTime from = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime to = toDate != null ? toDate.atTime(LocalTime.MAX) : null;
        return searchHistoryRepository.findAll(SearchHistorySpecifications.withFilters(from, to, type, text), Sort.by(Sort.Direction.DESC, "createdAt")).stream()
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
                .userId(searchHistory.getUser() != null ? searchHistory.getUser().getId() : null)
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

    // Resolves the associated user from the request user id or the text identifier.
    private Optional<User> resolveUser(Long userId, String userIdentifier) {
        if (userId != null) {
            return userRepository.findById(userId);
        }
        if (userIdentifier != null && !userIdentifier.isBlank()) {
            try {
                return userRepository.findById(Long.valueOf(userIdentifier));
            } catch (NumberFormatException ignored) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
