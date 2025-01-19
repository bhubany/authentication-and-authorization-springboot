package com.authauz.authauz.security.filter;

import java.io.IOException;
import java.util.Objects;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.authauz.authauz.configuration.AppConfig;
import com.authauz.authauz.security.token.JwtAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CookieBasedAuthenticationFilter extends OncePerRequestFilter {
    private AppConfig appConfig;
    private AuthenticationManager authenticationManager;

    public CookieBasedAuthenticationFilter(AuthenticationManager authenticationManager, AppConfig appConfig) {
        this.authenticationManager = authenticationManager;
        this.appConfig = appConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = getTokenFromCookies(request);

        if (Objects.isNull(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Authentication authentication = authenticationManager.authenticate(new JwtAuthenticationToken(token));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("Error occured : ", e);
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (!Objects.isNull(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(appConfig.getCookie().getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
