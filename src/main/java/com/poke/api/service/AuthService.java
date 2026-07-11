package com.poke.api.service;

import com.poke.api.dto.request.AuthRequest;
import com.poke.api.dto.response.AuthResponse;
import com.poke.api.dto.response.UserResponse;
import com.poke.api.model.User;
import com.poke.api.repository.UserRepository;
import com.poke.api.token.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Authenticates user and generates JWT token
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null || !user.isActive()) {
            return null;
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return null;
        }

        String token = jwtService.generateToken(user.getEmail());
        UserResponse userResponse = mapToResponse(user);

        return new AuthResponse(token, userResponse);
    }

    // Gets user by email
    public UserResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToResponse)
                .orElse(null);
    }

    // Maps User to UserResponse
    private UserResponse mapToResponse(User u) {
        UserResponse res = new UserResponse();
        res.setId(u.getId());
        res.setUsername(u.getUsername());
        res.setEmail(u.getEmail());
        res.setName(u.getName());
        return res;
    }
}