package com.authAuz.authAuz.service;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    public Claims getPayload(String jwt, SecretKey secretKey)
            throws SignatureException, ExpiredJwtException {
        try {
            if (Objects.isNull(jwt)) {
                throw new RuntimeException("authentication.token.missing");
            }
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .clockSkewSeconds(10)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
        } catch (SignatureException e) {
            throw new RuntimeException("authentication.token.invalid");
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("authentication.token.expired");
        }
    }

    /**
     * Generates a JWT token with the specified parameters.
     *
     * @param subject   The subject of the token (e.g., User Authentication ID).
     * @param id        A unique identifier for the token.
     * @param audience  Intended audience for the token.
     * @param expiresIn The token's expiration time in seconds.
     * @param secretKey The {@link SecretKey} used to sign the token.
     * @return The generated JWT token as a {@link String}.
     */
    public String generateToken(String subject, String audience, int expiresIn, SecretKey secretKey) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + expiresIn * 1000L);
        return Jwts.builder()
                .subject(subject)
                .id(UUID.randomUUID().toString())
                .audience()
                .add(audience)
                .and()
                .issuedAt(issuedAt)
                .notBefore(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public String generateToken(String subject, String audience, Map<String, ?> claims, int expiresIn,
            SecretKey secretKey) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + expiresIn * 1000L);
        log.debug("Generating JWT token for subject: {}, audience: {}, issuedAt: {}, expiresIn: {} seconds",
                subject, audience, issuedAt, expiresIn);
        var token = Jwts.builder()
                .subject(subject)
                .id(UUID.randomUUID().toString())
                .claims(claims)
                .audience()
                .add(audience)
                .and()
                .issuedAt(issuedAt)
                .notBefore(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
        log.debug("JWT token successfully generated for subject: {} with expiration: {}. Token: {}", subject,
                expiration, token);
        return token;
    }

    /**
     * Generates a {@link SecretKey} for HMAC SHA from the provided signing key
     *
     * @param signingKey signing key string.
     * @return The {@link SecretKey} for HMAC SHA.
     * @throws NotFoundException If the signing key is null.
     */
    public SecretKey generateSecretKey(String signingKey) throws RuntimeException {
        Optional.ofNullable(signingKey).orElseThrow(() -> new RuntimeException("authentication.key.notfound"));
        return Keys.hmacShaKeyFor(signingKey.getBytes(Charset.forName("utf8")));
    }
}
