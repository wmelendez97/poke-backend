package com.poke.api.service;

import com.poke.api.dto.request.AuthRequest;
import com.poke.api.dto.response.AuthResponse;
import com.poke.api.model.User;
import com.poke.api.repository.UserRepository;
import com.poke.api.token.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository UserRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User User;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    // Sets up the test environment before each test method.
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        User = new User();
        User.setId(1L);
        User.setName("Test User");
        User.setEmail("test@example.com");
        User.setPassword(passwordEncoder.encode("password123"));
        User.setActive(true);
    }

    @Test
    // Tests successful user login.
    void login_Success() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(UserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(User));
        when(jwtService.generateToken(anyLong(), anyString(), anyString(), anyString())).thenReturn("mock-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock-token", response.getToken());
        assertNotNull(response.getUser());
        assertEquals("Test User", response.getUser().getName());
    }

    @Test
    // Tests login with an invalid password.
    void login_InvalidPassword() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        when(UserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(User));

        AuthResponse response = authService.login(request);

        assertNull(response);
    }

    @Test
    // Tests login when the user is not found.
    void login_UserNotFound() {
        AuthRequest request = new AuthRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("password123");

        when(UserRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        AuthResponse response = authService.login(request);

        assertNull(response);
    }

    @Test
    // Tests login when the user is inactive.
    void login_InactiveUser() {
        User.setActive(false);
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(UserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(User));

        AuthResponse response = authService.login(request);

        assertNull(response);
    }
}
