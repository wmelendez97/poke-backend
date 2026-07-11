package com.poke.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Value("${external.api.base-url}")
    private String externalApiBaseUrl;

    @Value("${external.api.pokeapi.base-url}")
    private String pokeApiBaseUrl;

    // Builds a RestTemplate with base URL and timeouts for external API calls
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(externalApiBaseUrl)
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }

    // Builds a RestTemplate for PokeApiAPI with timeouts
    @Bean
    public RestTemplate pokeApiRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(pokeApiBaseUrl)
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }
}