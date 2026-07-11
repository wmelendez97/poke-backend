package com.poke.api.util;

public class ApiError {
	private String code;
	private String detail;

	public ApiError() {
	}

	public ApiError(String code, String detail) {
		this.code = code;
		this.detail = detail;
	}

	// Standard errors
	public static class ErrorCodes {
		public static final ApiError NOT_FOUND = new ApiError("NOT_FOUND", ApiMessages.ERROR_NOT_FOUND.getMessage());
		public static final ApiError BAD_REQUEST = new ApiError("BAD_REQUEST", ApiMessages.ERROR_VALIDATION.getMessage());
		public static final ApiError UNAUTHORIZED = new ApiError("UNAUTHORIZED",
				"You do not have permission to make this request");
		public static final ApiError FORBIDDEN = new ApiError("FORBIDDEN",
				"Access denied for the requested operation");
		public static final ApiError INTERNAL_ERROR = new ApiError("INTERNAL_ERROR", ApiMessages.ERROR_INTERNAL.getMessage());
		public static final ApiError DB_ERROR = new ApiError("DB_ERROR", ApiMessages.ERROR_DB.getMessage());
		public static final ApiError DUPLICATE = new ApiError("DUPLICATE", ApiMessages.ERROR_DUPLICATE.getMessage());
		public static final ApiError EXTERNAL_SERVICE_ERROR = new ApiError("EXTERNAL_SERVICE_ERROR",
				ApiMessages.ERROR_EXTERNAL_SERVICE.getMessage());
		public static final ApiError EXTERNAL_SERVICE_TIMEOUT = new ApiError("EXTERNAL_SERVICE_TIMEOUT",
				ApiMessages.ERROR_EXTERNAL_SERVICE_TIMEOUT.getMessage());
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
}