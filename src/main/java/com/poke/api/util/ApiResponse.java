package com.poke.api.util;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

public class ApiResponse<T> {

	private static final Logger log = LogManager.getLogger(ApiResponse.class);
	private static final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper()
			.configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
			.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
			.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
			.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);

	private T data;
	private boolean success;
	private String message;
	private int code;
	private HttpStatus status;
	private List<ApiError> errors;

	public ApiResponse() {
	}

	// Success with standard message
	public ApiResponse(T data) {
		this(data, ApiMessages.SUCCESS_ACTION.getMessage());
	}

	// Success with custom message
	public ApiResponse(T data, String message) {
		this.data = data;
		this.success = true;
		this.message = (message != null) ? message : ApiMessages.SUCCESS_ACTION.getMessage();
		this.code = HttpStatus.OK.value();
		this.status = HttpStatus.OK;
		this.errors = List.of();
		logResponse("GET", this.status, null);
	}

	// Error with standard message
	public ApiResponse(List<ApiError> errors) {
		this(ApiMessages.ERROR_PROCESS.getMessage(), errors, HttpStatus.BAD_REQUEST);
	}

	// Error with custom message
	public ApiResponse(String message, List<ApiError> errors) {
		this(message, errors, HttpStatus.BAD_REQUEST);
	}

	// Error with specific HTTP code
	public ApiResponse(String message, List<ApiError> errors, HttpStatus status) {
		this.data = null;
		this.success = false;
		this.message = (message != null) ? message : ApiMessages.ERROR_PROCESS.getMessage();
		this.code = status.value();
		this.status = status;
		this.errors = errors;
		logResponse("ERROR", this.status, errors);
	}

	// Automatic logging
	private void logResponse(String action, HttpStatus status, List<ApiError> errors) {
		StackTraceElement caller = null;
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			String cls = ste.getClassName();
			if (!cls.startsWith("java.") && !cls.startsWith("jdk.") && !cls.startsWith("org.springframework")
					&& !cls.equals(ApiResponse.class.getName())) {
				caller = ste;
				break;
			}
		}
		if (caller == null) {
			return;
		}
		String simpleName = caller.getClassName().substring(caller.getClassName().lastIndexOf('.') + 1);
		String method = caller.getMethodName();
		String txId = MDC.get("txId");
		String payload;
		try {
			payload = mapper.writeValueAsString(data);
		} catch (Exception e) {
			log.warn("Payload serialize error: {}", e.getMessage());
			payload = String.valueOf(data);
		}
		if (success) {
			log.info("{} {}.{} - Status: {} {} - x-transaction-id: {}", action, simpleName, method, status.value(),
					status.getReasonPhrase(), txId);
			log.info("Payload: {}", payload);
		} else {
			log.error("{} {}.{} - Status: {} {} - Message: {} - x-transaction-id: {}", action, simpleName, method,
					status.value(), status.getReasonPhrase(), message, txId);
			log.error("Payload: {}", payload);
			if (errors != null && !errors.isEmpty()) {
				errors.forEach(err -> log.error("[{}] {}", err.getCode(), err.getDetail()));
			}
		}
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	public int getCode() {
		return code;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public T getData() {
		return data;
	}

	public List<ApiError> getErrors() {
		return errors;
	}
}