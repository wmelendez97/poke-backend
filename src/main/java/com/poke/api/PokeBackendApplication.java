package com.poke.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class PokeBackendApplication {
	public static void main(String[] args) {
		// Main method to start the Spring Boot application
		SpringApplication.run(PokeBackendApplication.class, args);
	}
}