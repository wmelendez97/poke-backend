package com.poke.api.util.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.poke.api.util.ApiError;
import com.poke.api.util.ApiMessages;
import com.poke.api.util.ApiResponse;

@ControllerAdvice
public class SecurityExceptionHandler {

	// Invalid credentials
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
		ApiResponse<Object> response = new ApiResponse<>(ApiMessages.ERROR_UNAUTHORIZED.getMessage(),
				List.of(new ApiError("BAD_CREDENTIALS", "Invalid credentials")));
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	// Invalid token
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiResponse<Object>> handleAuthException(AuthenticationException ex) {
		ApiResponse<Object> response = new ApiResponse<>(ApiMessages.ERROR_UNAUTHORIZED.getMessage(),
				List.of(new ApiError("INVALID_TOKEN", "Invalid or expired authentication token")));
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	// User without sufficient permissions
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
		ApiResponse<Object> response = new ApiResponse<>(ApiMessages.ERROR_FORBIDDEN.getMessage(),
				List.of(new ApiError("ACCESS_DENIED", "You do not have permission to access this resource")));
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}
}