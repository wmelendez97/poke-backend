package com.poke.api.controller;

import com.poke.api.dto.request.AuthRequest;
import com.poke.api.dto.response.AuthResponse;
import com.poke.api.dto.response.UserResponse;
import com.poke.api.service.AuthService;
import com.poke.api.token.TokenRequired;
import com.poke.api.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and authorization")
public class AuthController {

    private final AuthService authService;
    private final HttpServletRequest request;

    // Authenticates user and returns JWT token
    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
        AuthResponse data = authService.login(authRequest);
        if (data != null) {
            return ResponseEntity.ok(new ApiResponse<>(data));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(null));
    }

    // Gets authenticated user information
    @GetMapping("/me")
    @TokenRequired
    @Operation(summary = "Get authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        Long userId = (Long) request.getAttribute("authenticatedUserId");
        String email = (String) request.getAttribute("authenticatedUserEmail");
        UserResponse data = authService.getUserByEmail(email);
        if (data != null) {
            return ResponseEntity.ok(new ApiResponse<>(data));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(null));
    }
}
