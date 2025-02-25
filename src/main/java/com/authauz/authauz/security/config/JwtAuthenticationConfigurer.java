package com.authauz.authauz.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import com.authauz.authauz.configuration.AppConfigurationProperties;
import com.authauz.authauz.security.filter.CookieBasedAuthenticationFilter;
import com.authauz.authauz.security.provider.JwtBasedAuthenticationProvider;

import lombok.RequiredArgsConstructor;

/**
 * Configures JWT-based authentication components, including the
 * AuthenticationManager and CookieBasedAuthenticationFilter, for secure request
 * processing.
 */
@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationConfigurer {
    private final AppConfigurationProperties appConfig;
    private final JwtBasedAuthenticationProvider jwtProvider;

    /**
     * Bean definition for AuthenticationManager, using
     * JwtBasedAuthenticationProvider
     * for token validation and user authentication.
     *
     * @param jwtProvider The custom authentication provider for JWT tokens.
     * @return An AuthenticationManager instance.
     */
    @Bean
    AuthenticationManager jwtAuthenticationManager(JwtBasedAuthenticationProvider jwtProvider) throws Exception {
        return new ProviderManager(jwtProvider);
    }

    /**
     * Bean definition for CookieBasedAuthenticationFilter, which handles
     * authentication by extracting the JWT token from cookies.
     *
     * @return A configured CookieBasedAuthenticationFilter instance.
     */
    @Bean
    CookieBasedAuthenticationFilter cookieBasedAuthenticationFilter() throws Exception {
        return new CookieBasedAuthenticationFilter(jwtAuthenticationManager(jwtProvider), appConfig);
    }
}
