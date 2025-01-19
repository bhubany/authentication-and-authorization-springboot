package com.authauz.authauz.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.authauz.authauz.configuration.AppConfigurationProperties;
import com.authauz.authauz.dto.AuthRequest;
import com.authauz.authauz.dto.AuthResponse;
import com.authauz.authauz.utils.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtils jwtUtils;
    private final AppConfigurationProperties properties;

    public AuthResponse authenticate(AuthRequest request) {
        validateAuthRequest(request);
        String username = request.getUsername();
        String password = request.getPassword();

        boolean isUserValid = Objects.equals(username, "user") && Objects.equals(password, "password");
        if (!isUserValid) {
            throw new RuntimeException("Invalid username or password");
        }

        var secretKey = jwtUtils.generateSecretKey(properties.getJwt().getSecret());

        String token = jwtUtils.generateToken(username, username, properties.getJwt().getExpiresIn(), secretKey);
        return AuthResponse.builder().username(username).token(token).build();
    }

    private void validateAuthRequest(AuthRequest request) {
        Objects.requireNonNull(request, "Username and password cannot be null");
        Objects.requireNonNull(request.getUsername(), "Username cannot be null");
        Objects.requireNonNull(request.getPassword(), "Password cannot be null");
    }
}
