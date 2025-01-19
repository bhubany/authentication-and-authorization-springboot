package com.authauz.authauz.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import com.authauz.authauz.configuration.AppConfigurationProperties;
import com.authauz.authauz.security.filter.CookieBasedAuthenticationFilter;
import com.authauz.authauz.security.provider.JwtBasedAuthenticationProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationConfigurer {
    private final AppConfigurationProperties appConfig;
    private final JwtBasedAuthenticationProvider jwtProvider;

    @Bean
    AuthenticationManager jwtAuthenticationManager(JwtBasedAuthenticationProvider jwtProvider) throws Exception {
        return new ProviderManager(jwtProvider);
    }

    @Bean
    CookieBasedAuthenticationFilter cookieBasedAuthenticationFilter() throws Exception {
        return new CookieBasedAuthenticationFilter(jwtAuthenticationManager(jwtProvider), appConfig);
    }
}
