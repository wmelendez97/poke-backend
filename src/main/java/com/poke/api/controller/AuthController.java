package com.poke.api.controller;

import com.poke.api.dto.request.AuthRequest;
import com.poke.api.dto.response.AuthResponse;
import com.poke.api.dto.response.UserResponse;
import com.poke.api.service.AuthService;
import com.poke.api.token.TokenRequired;
import com.poke.api.util.ApiError;
import com.poke.api.util.ApiMessages;
import com.poke.api.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and authorization")
public class AuthController {

    private final AuthService authService;

    // Authenticates user and returns JWT token
    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        AuthResponse data = authService.login(request);
        return (data != null)
                ? ResponseEntity.ok(new ApiResponse<>(data))
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(ApiMessages.ERROR_UNAUTHORIZED.getMessage(),
                        List.of(ApiError.ErrorCodes.UNAUTHORIZED), HttpStatus.UNAUTHORIZED));
    }

    // Gets authenticated user information
    @GetMapping("/me")
    @TokenRequired
    @Operation(summary = "Get authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserResponse>> me(HttpServletRequest request) {
        String email = (String) request.getAttribute("authenticatedUser");
        UserResponse data = authService.getUserByEmail(email);
        return (data != null)
                ? ResponseEntity.ok(new ApiResponse<>(data))
                : ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(ApiMessages.ERROR_NOT_FOUND.getMessage(),
                        List.of(ApiError.ErrorCodes.NOT_FOUND), HttpStatus.NOT_FOUND));
    }
}