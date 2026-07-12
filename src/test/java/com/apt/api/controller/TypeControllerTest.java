package com.apt.api.controller;

import com.poke.api.controller.TypeController;
import com.poke.api.dto.response.PokemonResponse;
import com.poke.api.dto.response.TypeSummaryResponse;
import com.poke.api.service.SearchHistoryService;
import com.poke.api.service.TypeService;
import com.poke.api.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypeControllerTest {

    @Mock
    private TypeService typeService;

    @Mock
    private SearchHistoryService searchHistoryService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private TypeController typeController;

    @Test
    // Verifies the controller returns the type catalog.
    void getAllTypes_Success() {
        when(request.getAttribute("authenticatedUserId")).thenReturn(1L);
        when(request.getAttribute("authenticatedUserUsername")).thenReturn("testuser");
        when(request.getRequestURI()).thenReturn("/types");
        when(typeService.getAllTypes("testuser")).thenReturn(List.of(TypeSummaryResponse.builder().id(13L).name("electric").build()));

        ResponseEntity<?> response = typeController.getAllTypes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    // Verifies the controller returns Pokemon by type.
    void getPokemonByType_Success() {
        when(request.getAttribute("authenticatedUserId")).thenReturn(1L);
        when(request.getAttribute("authenticatedUserUsername")).thenReturn("testuser");
        when(request.getRequestURI()).thenReturn("/types/electric/pokemon");
        when(typeService.getPokemonByType("electric", "testuser")).thenReturn(List.of(PokemonResponse.builder().id(25L).name("pikachu").build()));

        ResponseEntity<?> response = typeController.getPokemonByType("electric");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertNotNull(apiResponse.getData());
    }
}
