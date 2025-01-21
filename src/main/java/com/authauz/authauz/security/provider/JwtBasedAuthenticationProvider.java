package com.authauz.authauz.security.provider;

import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.authauz.authauz.common.RequestContext;
import com.authauz.authauz.configuration.AppConfigurationProperties;
import com.authauz.authauz.security.token.JwtAuthenticationToken;
import com.authauz.authauz.utils.JwtUtils;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

/**
 * JwtBasedAuthenticationProvider is an implementation of the
 * AuthenticationProvider
 * interface that handles authentication based on JWT tokens.
 * 
 * This class:
 * - Parses and validates the JWT token using {@link JwtUtils}.
 * - Extracts user-specific details (like userId) from the token claims.
 * - Builds a {@link RequestContext} object for authenticated users.
 * - Returns an authenticated {@link JwtAuthenticationToken} if the token is
 * valid.
 */
@Component
@RequiredArgsConstructor
public class JwtBasedAuthenticationProvider implements AuthenticationProvider {
    private final JwtUtils jwtUtils;
    private final AppConfigurationProperties appConfig;
    private final UserContextBuilder ctxBuilder;

    /**
     * Authenticates the given authentication request by validating the provided
     * JWT token. If the token is valid, the method extracts user details,
     * constructs a {@link RequestContext}, and returns an authenticated token.
     * 
     * @param authentication The authentication request object containing the JWT
     *                       token.
     * @return An authenticated {@link JwtAuthenticationToken} with the user
     *         context.
     * @throws AuthenticationException If the authentication process fails (e.g.,
     *                                 invalid token).
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
        String token = auth.getToken();

        SecretKey secretKey = jwtUtils.generateSecretKey(appConfig.getJwt().getSecret());
        Claims claims = jwtUtils.getPayload(auth.getToken(), secretKey);

        UUID userId = UUID.fromString(claims.getAudience().stream().toList().get(0));

        RequestContext principal = ctxBuilder.prepareContext(userId);
        return new JwtAuthenticationToken(token, principal);
    }

    /**
     * Determines whether this AuthenticationProvider supports the given
     * authentication type.
     * 
     * @param authentication The type of authentication object.
     * @return True if the authentication object is a JwtAuthenticationToken, false
     *         otherwise.
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
