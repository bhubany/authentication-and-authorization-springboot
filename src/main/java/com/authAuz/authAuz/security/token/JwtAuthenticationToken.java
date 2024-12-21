package com.authAuz.authAuz.security.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

import com.authAuz.authAuz.security.common.RequestContext;

import lombok.Getter;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private RequestContext principal;

    @Getter
    private final String token;

    // to initialize and set JWT token
    public JwtAuthenticationToken(String token) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.token = token;
        this.principal = null;
        super.setAuthenticated(false);
    }

    // to set authenticated user details
    public JwtAuthenticationToken(String token, RequestContext principal) {
        super(null);
        this.token = token;
        this.principal = principal;
        super.setAuthenticated(true);
        super.setDetails(principal);
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public boolean isAuthenticated() {
        return super.isAuthenticated();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Not allowed to set authentication manually, use constructor instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

}
