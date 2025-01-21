package com.authauz.authauz.security.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

import com.authauz.authauz.common.RequestContext;

import lombok.Getter;

/**
 * JwtAuthenticationToken is a custom implementation of
 * {@link AbstractAuthenticationToken}
 * designed to handle JWT-based authentication.
 * 
 * This class:
 * - Represents both unauthenticated and authenticated states for JWT tokens.
 * - Stores the JWT token and the associated {@link RequestContext} for
 * authenticated users.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    // The principal represents the user context (authenticated user details)
    private RequestContext principal;

    // The raw JWT token (used for authentication purposes)
    @Getter
    private final String token;

    /**
     * Constructor for initializing an unauthenticated token.
     * 
     * @param token The raw JWT token.
     */
    public JwtAuthenticationToken(String token) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.token = token;
        this.principal = null;
        super.setAuthenticated(false);
    }

    /**
     * Constructor for initializing an authenticated token.
     * 
     * @param token     The raw JWT token.
     * @param principal The user context representing authenticated user details.
     */
    public JwtAuthenticationToken(String token, RequestContext principal) {
        super(null);
        this.token = token;
        this.principal = principal;
        super.setAuthenticated(true); // Mark as authenticated
        super.setDetails(principal);
    }

    /**
     * Returns the user context (principal) for authenticated tokens.
     * 
     * @return The {@link RequestContext} representing the authenticated user
     *         details.
     */
    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    /**
     * Returns the authentication status of the token.
     * 
     * @return True if the token is authenticated; otherwise, false.
     */
    @Override
    public boolean isAuthenticated() {
        return super.isAuthenticated();
    }

    /**
     * Ensures that the authentication status can only be set via constructors.
     * 
     * @param isAuthenticated The new authentication status.
     * @throws IllegalArgumentException If an attempt is made to set authentication
     *                                  manually.
     */
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Not allowed to set authentication manually, use constructor instead");
        }
        super.setAuthenticated(false);
    }

    /**
     * Returns the raw JWT token, which acts as the credentials for authentication.
     * 
     * @return The JWT token.
     */
    @Override
    public Object getCredentials() {
        return this.token;
    }

}
