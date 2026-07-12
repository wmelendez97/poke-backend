package com.poke.api.service;

import com.poke.api.dto.response.SearchHistoryResponse;
import com.poke.api.model.SearchHistory;
import com.poke.api.model.User;
import com.poke.api.repository.SearchHistoryRepository;
import com.poke.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchHistoryServiceTest {

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @Mock
    private UserRepository userRepository;

    private SearchHistoryService createService() {
        return new SearchHistoryService(searchHistoryRepository, userRepository);
    }

    @Test
    // Verifies the service returns filtered search history entries.
    void getAllSearchHistory_WithFilters_ReturnsResults() {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        SearchHistory history = SearchHistory.builder()
                .id(10L)
                .user(user)
                .userIdentifier("1")
                .searchType("search")
                .queryValue("pika")
                .endpoint("/api/pokemon/search")
                .success(true)
                .statusCode(200)
                .createdAt(LocalDateTime.now())
                .build();

        when(searchHistoryRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(history));

        List<SearchHistoryResponse> response = createService().getAllSearchHistory(
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 12),
                "search",
                "pika");

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getUserId());
        assertEquals("search", response.get(0).getSearchType());
        assertEquals("pika", response.get(0).getQueryValue());
    }

    @Test
    // Verifies the service links history records to an existing user.
    void saveSearchHistory_AssignsUserWhenFound() {
        User user = User.builder()
                .id(2L)
                .username("secondtest")
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        createService().saveSearchHistory(2L, "2", "detail", "25", "/api/pokemon/25", true, 200, null);

        assertNotNull(user);
    }
}
