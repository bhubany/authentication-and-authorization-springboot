package com.authAuz.authAuz.security.provider;

import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.authAuz.authAuz.configuration.AppConfig;
import com.authAuz.authAuz.security.common.RequestContext;
import com.authAuz.authAuz.security.token.JwtAuthenticationToken;
import com.authAuz.authAuz.service.JwtService;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtBasedAuthenticationProvider implements AuthenticationProvider {
    private final JwtService jwtService;
    private final AppConfig appConfig;
    private final UserContextBuilder ctxBuilder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
        String token = auth.getToken();

        SecretKey secretKey = jwtService.generateSecretKey(appConfig.getJwt().getSecret());
        Claims claims = jwtService.getPayload(auth.getToken(), secretKey);

        UUID authId = UUID.fromString(claims.getSubject());
        UUID sellerLocationId = UUID.fromString(claims.getAudience().stream().toList().get(0));

        RequestContext principal = ctxBuilder.prepareContext(authId, sellerLocationId);
        return new JwtAuthenticationToken(token, principal);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
