package com.poke.api.token;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import com.poke.api.util.ApiMessages;
import com.poke.api.util.ApiResponse;

@Aspect
@Component
public class TokenRequiredValidationAspect {

	private final JwtService jwtService;

	// Constructor for TokenRequiredValidationAspect, injecting JwtService.
	public TokenRequiredValidationAspect(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	// Validates JWT, extracts user and makes it available in request and logs
	@Before("@annotation(com.poke.api.token.TokenRequired)")
	public void validateToken(JoinPoint joinPoint) {

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new TokenValidationException(
					new ApiResponse<>(ApiMessages.ERROR_TOKEN_MISSING.getMessage(), null, HttpStatus.UNAUTHORIZED));
		}

		String token = authHeader.substring(7);

		if (!jwtService.validateToken(token)) {
			throw new TokenValidationException(
					new ApiResponse<>(ApiMessages.ERROR_TOKEN_INVALID.getMessage(), null, HttpStatus.UNAUTHORIZED));
		}

		String email = jwtService.extractUser(token);
		Long userId = jwtService.extractClaim(token, claims -> {
			Object id = claims.get("id");
			if (id instanceof Number number) {
				return number.longValue();
			}
			if (id instanceof String value && !value.isBlank()) {
				return Long.valueOf(value);
			}
			return null;
		});
		String username = jwtService.extractClaim(token, claims -> (String) claims.get("username"));
		String name = jwtService.extractClaim(token, claims -> (String) claims.get("name"));

		Map<String, Object> authenticatedUser = new LinkedHashMap<>();
		authenticatedUser.put("id", userId);
		authenticatedUser.put("username", username);
		authenticatedUser.put("email", email);
		authenticatedUser.put("name", name);

		request.setAttribute("authenticatedUser", email);
		request.setAttribute("authenticatedUserId", userId);
		request.setAttribute("authenticatedUserUsername", username);
		request.setAttribute("authenticatedUserEmail", email);
		request.setAttribute("authenticatedUserName", name);
		request.setAttribute("authenticatedUserData", authenticatedUser);
		MDC.put("user", email);
	}

	// Clears logging context after request finishes
	@After("@annotation(com.poke.api.token.TokenRequired)")
	public void clearContext() {
		MDC.clear();
	}
}
