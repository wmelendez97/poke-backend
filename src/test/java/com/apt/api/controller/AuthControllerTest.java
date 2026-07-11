package com.poke.api.controller;

import com.poke.api.dto.request.AuthRequest;
import com.poke.api.dto.response.AuthResponse;
import com.poke.api.dto.response.UserResponse;
import com.poke.api.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private AuthController authController;

    private AuthRequest authRequest;
    private AuthResponse authResponse;
    private UserResponse UserResponse;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");

        UserResponse = new UserResponse();
        UserResponse.setId(1L);
        UserResponse.setName("Test User");
        UserResponse.setEmail("test@example.com");

        authResponse = new AuthResponse("mock-token", UserResponse);
    }

    @Test
    void login_Success() {
        when(authService.login(authRequest)).thenReturn(authResponse);

        ResponseEntity response = authController.login(authRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void login_Failure() {
        when(authService.login(authRequest)).thenReturn(null);

        ResponseEntity response = authController.login(authRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void me_Success() {
        when(httpRequest.getAttribute("authenticatedUser")).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(UserResponse);

        ResponseEntity response = authController.me(httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void me_NotFound() {
        when(httpRequest.getAttribute("authenticatedUser")).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(null);

        ResponseEntity response = authController.me(httpRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}