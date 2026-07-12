package com.poke.api.controller;

import com.poke.api.controller.SearchHistoryController;
import com.poke.api.dto.response.SearchHistoryResponse;
import com.poke.api.service.SearchHistoryService;
import com.poke.api.util.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchHistoryControllerTest {

    @Mock
    private SearchHistoryService searchHistoryService;

    @InjectMocks
    private SearchHistoryController searchHistoryController;

    @Test
    // Verifies the controller returns the global search history.
    void getGlobalSearchHistory_Success() {
        when(searchHistoryService.getAllSearchHistory(null, null, null, null)).thenReturn(List.of(SearchHistoryResponse.builder()
                .id(1L)
                .userIdentifier("1")
                .searchType("detail")
                .queryValue("25")
                .endpoint("/api/pokemon/25")
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .createdAt(LocalDateTime.now())
                .build()));

        ResponseEntity<?> response = searchHistoryController.getGlobalSearchHistory();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertNotNull(apiResponse.getData());
    }

    @Test
    // Verifies the controller returns the filtered global search history.
    void getGlobalSearchHistory_WithFilters_Success() {
        LocalDate from = LocalDate.of(2026, 7, 1);
        LocalDate to = LocalDate.of(2026, 7, 12);
        when(searchHistoryService.getAllSearchHistory(from, to, "search", "pika")).thenReturn(List.of(SearchHistoryResponse.builder()
                .id(2L)
                .userIdentifier("1")
                .searchType("search")
                .queryValue("pika")
                .endpoint("/api/pokemon/search")
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .createdAt(LocalDateTime.now())
                .build()));

        ResponseEntity<?> response = searchHistoryController.getGlobalSearchHistory(from, to, "search", "pika");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertNotNull(apiResponse.getData());
    }

    @Test
    // Verifies the controller returns the history for one user.
    void getSearchHistoryByUser_Success() {
        when(searchHistoryService.getSearchHistoryByUser("1")).thenReturn(List.of(SearchHistoryResponse.builder()
                .id(1L)
                .userIdentifier("1")
                .searchType("detail")
                .queryValue("25")
                .endpoint("/api/pokemon/25")
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .createdAt(LocalDateTime.now())
                .build()));

        ResponseEntity<?> response = searchHistoryController.getSearchHistoryByUser("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertNotNull(apiResponse.getData());
    }
}
