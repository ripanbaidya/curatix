package com.curatix.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Global CORS (Cross-Origin Resource Sharing) configuration.
 * <p>Defines allowed origins, HTTP methods, headers, and credentials
 * for frontend or mobile clients interacting with the backend APIs.
 */
public class CorsConfig {

    private static final List<String> ALLOWED_METHODS =
            List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD");
    private static final List<String> ALLOWED_ORIGINS =
            List.of("http://localhost:3000", "http://localhost:5173");
    private static final List<String> ALLOWED_HEADERS =
            List.of("Authorization", "Content-Type", "Accept", "X-Requested-With");
    private static final List<String> EXPOSED_HEADERS =
            List.of("Authorization", "X-Total-Count");

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();

        config.setAllowedOrigins(ALLOWED_ORIGINS);
        config.setAllowedMethods(ALLOWED_METHODS);
        config.setAllowedHeaders(ALLOWED_HEADERS);
        config.setExposedHeaders(EXPOSED_HEADERS);
        config.setAllowCredentials(true); // Allow cookies and authorization headers
        config.setMaxAge(3600L); // Cache preflight response for 1 hour

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
