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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ObjectMapper objectMapper;
    private final AppAuthenticationConfigurer appAuthenticationConfigurer;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // TODO: handle csrf for other than GET methods
                .with(appAuthenticationConfigurer, Customizer.withDefaults())
                .anonymous(AnonymousConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling((e) -> {
                    e.authenticationEntryPoint((req, resp, authException) -> {
                        prepareUnauthorizedResponse(resp, HttpStatus.UNAUTHORIZED,
                                authException);
                    });
                });

        return http.build();
    }

    private void prepareUnauthorizedResponse(HttpServletResponse response, HttpStatus errorType,
            AuthenticationException exception)
            throws IOException {
        System.out.println(exception);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        ResponseEntity<Object> apiResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        // .status(HttpStatus.UNAUTHORIZED)
        // .error(APIResponse.APIError.builder()
        // .type(errorType.getType())
        // .code(errorType.getCode())
        // .error_user_title("Unauthorized access")
        // .error_user_msg("Unauthorized access: either token is invalid or expired")
        // .message(exception.getMessage())
        // .build())
        // .build();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
