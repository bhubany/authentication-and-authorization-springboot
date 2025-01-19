package com.authauz.authauz.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtils {

    /**
     * Retrieves the payload (claims) from a JWT token.
     *
     * @param jwt       The JWT token to parse.
     * @param secretKey The {@link SecretKey} used for signature verification.
     * @return Parsed {@link Claims} from the token.
     * @throws IllegalArgumentException If the token is missing, invalid, or
     *                                  expired.
     */
    public Claims getPayload(String jwt, SecretKey secretKey) {
        Objects.requireNonNull(secretKey, "Secret key must not be null");

        try {
            if (Objects.isNull(jwt)) {
                throw new IllegalArgumentException("Authentication token is missing.");
            }

            return Jwts.parser()
                    .verifyWith(secretKey)
                    .clockSkewSeconds(10)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
        } catch (SignatureException e) {
            throw new IllegalArgumentException("Authentication token is invalid.", e);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Authentication token has expired.", e);
        }
    }

    /**
     * Generates a JWT token with the specified parameters.
     *
     * @param subject   The subject of the token (e.g., User Authentication ID).
     * @param audience  Intended audience for the token.
     * @param expiresIn The token's expiration time in seconds.
     * @param secretKey The {@link SecretKey} used to sign the token.
     * @return The generated JWT token as a {@link String}.
     * @throws IllegalArgumentException If any required parameters are null or
     *                                  invalid.
     */
    public String generateToken(String subject, String audience, int expiresIn, SecretKey secretKey) {
        validateInputs(subject, audience, secretKey);

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

    /**
     * Generates a JWT token with custom claims.
     *
     * @param subject   The subject of the token.
     * @param audience  Intended audience for the token.
     * @param claims    Additional claims to include in the token.
     * @param expiresIn The token's expiration time in seconds.
     * @param secretKey The {@link SecretKey} used to sign the token.
     * @return The generated JWT token as a {@link String}.
     * @throws IllegalArgumentException If any required parameters are null or
     *                                  invalid.
     */
    public String generateToken(String subject, String audience, Map<String, ?> claims, int expiresIn,
            SecretKey secretKey) {
        validateInputs(subject, audience, secretKey);

        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + expiresIn * 1000L);

        log.debug("Generating JWT token for subject: {}, audience: {}, issuedAt: {}, expiresIn: {} seconds",
                subject, audience, issuedAt, expiresIn);

        return Jwts.builder()
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
    }

    /**
     * Generates a {@link SecretKey} for HMAC SHA from the provided signing key.
     *
     * @param signingKey The signing key string.
     * @return The {@link SecretKey} for HMAC SHA.
     * @throws IllegalArgumentException If the signing key is null or empty.
     */
    public SecretKey generateSecretKey(String signingKey) {
        if (Objects.isNull(signingKey) || signingKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Authentication signing key is missing.");
        }
        return Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Validates common inputs for token generation.
     *
     * @param subject   The subject of the token.
     * @param audience  Intended audience for the token.
     * @param secretKey The {@link SecretKey} used for signing.
     * @throws IllegalArgumentException If any required parameters are null.
     */
    private void validateInputs(String subject, String audience, SecretKey secretKey) {
        Objects.requireNonNull(subject, "Token subject must not be null");
        Objects.requireNonNull(audience, "Token audience must not be null");
        Objects.requireNonNull(secretKey, "Secret key must not be null");
    }
}
