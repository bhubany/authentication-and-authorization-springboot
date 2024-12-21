package com.authAuz.authAuz.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AppAuthenticationConfigurer extends AbstractHttpConfigurer<AppAuthenticationConfigurer, HttpSecurity> {
    private final JwtAuthenticationConfigurer jwtAuthenticationConfigurer;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtAuthenticationConfigurer.cookieBasedAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class);

    }

}
