package com.authauz.authauz.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.authauz.authauz.common.Role;
import com.authauz.authauz.common.UserType;
import com.authauz.authauz.configuration.AppConfigurationProperties;
import com.authauz.authauz.dto.AuthRequest;
import com.authauz.authauz.dto.AuthResponse;
import com.authauz.authauz.utils.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AuthService is responsible for handling authentication logic, including
 * validating the
 * user's credentials, generating a JWT token, and returning an authentication
 * response.
 * This service is currently using hardcoded username and password for
 * simplicity and
 * demonstration purposes. In production, this should be replaced with dynamic
 * user
 * validation from a user database or another reliable source.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtils jwtUtils;
    private final AppConfigurationProperties properties;

    /**
     * Authenticates a user based on the provided credentials. If the credentials
     * match
     * the hardcoded values, it generates a JWT token with the user's role and type.
     * 
     * @param request The authentication request containing username and password.
     * @return AuthResponse containing the username and generated JWT token.
     * @throws RuntimeException if invalid username or password is provided.
     */

    public AuthResponse authenticate(AuthRequest request) {
        validateAuthRequest(request);
        String username = request.getUsername();
        String password = request.getPassword();

        boolean isUserValid = username.equalsIgnoreCase("user") && Objects.equals(password, "password");

        if (!isUserValid) {
            log.error("Invalid username or password for user: {}", username);
            throw new RuntimeException("Invalid username or password");
        }

        // Hardcoded userType and role
        var userType = UserType.SELLER;
        var role = Role.ADMIN;

        var secretKey = jwtUtils.generateSecretKey(properties.getJwt().getSecret());

        Map<String, String> claims = new HashMap<>();
        claims.put("userType", userType.toString());
        claims.put("role", role.toString());

        // For demonstration purposes, a random UUID is generated to serve as the
        // audience. In a real application, this should be replaced with the actual user
        // ID to indicate the intended recipient of the JWT. The audience claim
        // typically refers to the entity for whom(recipients) the JWT is being issued,
        // often the user or service consuming the token.
        String audience = UUID.randomUUID().toString();

        String token = jwtUtils.generateToken(username, audience, claims, properties.getJwt().getExpiresIn(),
                secretKey);
        return AuthResponse.builder().username(username).userType(userType).role(role).token(token).build();
    }

    private void validateAuthRequest(AuthRequest request) {
        Objects.requireNonNull(request, "Username and password cannot be null");
        Objects.requireNonNull(request.getUsername(), "Username cannot be null");
        Objects.requireNonNull(request.getPassword(), "Password cannot be null");
    }

}
