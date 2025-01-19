package com.authauz.authauz.security.provider;

import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.authauz.authauz.configuration.AppConfigurationProperties;
import com.authauz.authauz.security.common.RequestContext;
import com.authauz.authauz.security.token.JwtAuthenticationToken;
import com.authauz.authauz.utils.JwtUtils;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtBasedAuthenticationProvider implements AuthenticationProvider {
    private final JwtUtils jwtUtils;
    private final AppConfigurationProperties appConfig;
    private final UserContextBuilder ctxBuilder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
        String token = auth.getToken();

        SecretKey secretKey = jwtUtils.generateSecretKey(appConfig.getJwt().getSecret());
        Claims claims = jwtUtils.getPayload(auth.getToken(), secretKey);

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
