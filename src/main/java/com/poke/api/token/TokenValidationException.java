package com.poke.api.token;

import com.poke.api.util.ApiResponse;

public class TokenValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final ApiResponse<Object> response;

	public TokenValidationException(ApiResponse<Object> response) {
		this.response = response;
	}

	public ApiResponse<Object> getResponse() {
		return response;
	}
}