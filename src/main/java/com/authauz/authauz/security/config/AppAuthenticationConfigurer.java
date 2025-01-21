package com.authauz.authauz.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * Custom configuration for integrating JWT authentication filter into the
 * Spring Security filter chain.
 */
@Configuration
@RequiredArgsConstructor
public class AppAuthenticationConfigurer extends AbstractHttpConfigurer<AppAuthenticationConfigurer, HttpSecurity> {
    private final JwtAuthenticationConfigurer jwtAuthenticationConfigurer;

    /**
     * Configures the HttpSecurity object to add the custom
     * CookieBasedAuthenticationFilter
     * before the UsernamePasswordAuthenticationFilter in the filter chain.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception in case of any errors during configuration
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtAuthenticationConfigurer.cookieBasedAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class);
    }
}
