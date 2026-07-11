package com.poke.api.util.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.poke.api.token.TokenValidationException;
import com.poke.api.util.ApiError;
import com.poke.api.util.ApiMessages;
import com.poke.api.util.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

	// Generic unhandled error
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
		ApiResponse<Object> response = new ApiResponse<>(ApiMessages.ERROR_INTERNAL.getMessage(),
				List.of(ApiError.ErrorCodes.INTERNAL_ERROR, new ApiError("EXCEPTION", ex.getMessage())));
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	// Validation error
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
		List<ApiError> validationErrors = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> new ApiError("VALIDATION_ERROR", error.getField() + ": " + error.getDefaultMessage()))
				.collect(Collectors.toList());

		ApiResponse<Object> response = new ApiResponse<>(ApiMessages.ERROR_VALIDATION.getMessage(), validationErrors);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	// Database integrity error
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiResponse<Object>> handleDBException(DataIntegrityViolationException ex) {
		ApiResponse<Object> response = new ApiResponse<>(ApiMessages.ERROR_DB.getMessage(), List.of(ApiError.ErrorCodes.DB_ERROR,
				new ApiError("DB_CAUSE", ex.getMostSpecificCause().getMessage())));
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	// JWT token validation error
	@ExceptionHandler(TokenValidationException.class)
	public ResponseEntity<ApiResponse<Object>> handleTokenException(TokenValidationException ex) {
		return ResponseEntity.status(ex.getResponse().getStatus()).body(ex.getResponse());
	}
}