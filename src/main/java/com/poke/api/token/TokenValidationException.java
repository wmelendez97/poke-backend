package com.poke.api.token;

import com.poke.api.util.ApiResponse;

public class TokenValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final ApiResponse<Object> response;

	// Constructor that takes an ApiResponse object.
	public TokenValidationException(ApiResponse<Object> response) {
		this.response = response;
	}

	// Returns the ApiResponse object associated with this exception.
	public ApiResponse<Object> getResponse() {
		return response;
	}
}