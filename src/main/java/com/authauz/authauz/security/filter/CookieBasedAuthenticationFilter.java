package com.authauz.authauz.security.filter;

import java.io.IOException;
import java.util.Objects;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.authauz.authauz.configuration.AppConfigurationProperties;
import com.authauz.authauz.security.token.JwtAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * CookieBasedAuthenticationFilter is a custom Spring Security filter
 * responsible for extracting a JWT from cookies, authenticating the token,
 * and setting the authentication in the security context.
 * 
 * This filter runs once per request and ensures that authenticated requests
 * have a valid JWT token before proceeding further in the filter chain.
 */
@Slf4j
@Component
public class CookieBasedAuthenticationFilter extends OncePerRequestFilter {
    private AppConfigurationProperties appProperties;
    private AuthenticationManager authenticationManager;

    public CookieBasedAuthenticationFilter(AuthenticationManager authenticationManager,
            AppConfigurationProperties appConfig) {
        this.authenticationManager = authenticationManager;
        this.appProperties = appConfig;
    }

    /**
     * Extracts the JWT token from cookies, attempts authentication, and sets the
     * authentication in the security context if successful.
     * 
     * If no token is found or authentication fails, the request continues without
     * authentication.
     * 
     * @param request     The HTTP request.
     * @param response    The HTTP response.
     * @param filterChain The filter chain to proceed with the next filter.
     * @throws ServletException In case of servlet-related errors.
     * @throws IOException      In case of I/O errors.
     */
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

    /**
     * Retrieves the JWT token from the cookies in the incoming HTTP request.
     * 
     * @param request The HTTP request containing cookies.
     * @return The JWT token if present; otherwise, null.
     */
    private String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (!Objects.isNull(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(appProperties.getCookie().getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
