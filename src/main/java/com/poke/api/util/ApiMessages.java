package com.poke.api.util;

public enum ApiMessages {

	SUCCESS_ACTION("Request completed successfully"),
	SUCCESS_CREATION("Record created successfully"),
	SUCCESS_UPDATE("Record updated successfully"),
	SUCCESS_ACTIVATION("Record activated successfully"),
	SUCCESS_INACTIVATION("Record deactivated successfully"),

	ERROR_PROCESS("Request could not be processed"),
	ERROR_VALIDATION("Request is invalid or incomplete"),
	ERROR_INTERNAL("Internal error occurred while processing the request"),
	ERROR_DB("Request could not be completed due to data integrity issue"),
	ERROR_NOT_FOUND("Requested resource not found"),
	ERROR_DUPLICATE("Record already exists"),

	ERROR_EXTERNAL_SERVICE("Error consuming external service"),
	ERROR_EXTERNAL_SERVICE_TIMEOUT("Timeout while consuming external service"),

	ERROR_UNAUTHORIZED("Unauthorized access"),
	ERROR_FORBIDDEN("Permission denied"),

	ERROR_TOKEN_EXPIRED("Your current session has expired. Please log in again."),
	ERROR_TOKEN_INVALID("Could not validate the request. Please log in again."),
	ERROR_TOKEN_MISSING("Session not started. Please log in to continue."),

	ERROR_USER_NOT_REGISTERED("User is not registered in the system"),
	ERROR_USER_INACTIVE("User is inactive in the system");

	private final String message;

	ApiMessages(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return message;
	}
}