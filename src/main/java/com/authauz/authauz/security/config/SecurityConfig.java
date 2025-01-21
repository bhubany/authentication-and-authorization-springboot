package com.authauz.authauz.security.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Security configuration class for setting up the application's security
 * filters.
 * <p>
 * This class leverages Spring Security's fluent API to configure features like
 * CSRF protection, custom authentication handlers, and exception handling.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ObjectMapper objectMapper;
    private final AppAuthenticationConfigurer appAuthenticationConfigurer;

    /**
     * Configures the security filter chain for the application.
     *
     * @param http the {@link HttpSecurity} object to configure.
     * @return the built {@link SecurityFilterChain}.
     * @throws Exception if there is an error in the configuration.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection for stateless API

                // Apply custom authentication configurations
                .with(appAuthenticationConfigurer, Customizer.withDefaults())

                // Disable anonymous access (enforces authentication for all endpoints)
                .anonymous(AnonymousConfigurer::disable)

                // Disable form-based login (JWT or cookie-based authentication is used)
                .formLogin(AbstractHttpConfigurer::disable)

                // Handle authentication exceptions and unauthorized access
                .exceptionHandling((e) -> {
                    e.authenticationEntryPoint((req, resp, authException) -> {
                        prepareUnauthorizedResponse(resp, HttpStatus.UNAUTHORIZED,
                                authException);
                    });
                });

        return http.build();
    }

    /**
     * Prepares an HTTP response for unauthorized access scenarios.
     *
     * @param response  the {@link HttpServletResponse} to write the response to.
     * @param errorType the HTTP status code representing the error type.
     * @param exception the authentication exception encountered.
     * @throws IOException if an error occurs while writing the response.
     */
    private void prepareUnauthorizedResponse(HttpServletResponse response, HttpStatus errorType,
            AuthenticationException exception) throws IOException {
        // Set the HTTP response status and content type
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        // Prepare the response body with an error message
        var apiResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized access: either token is invalid or expired");

        // Write the response body as JSON
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
