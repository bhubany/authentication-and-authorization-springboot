package com.authauz.authauz.security.provider;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.authauz.authauz.common.RequestContext;
import com.authauz.authauz.common.Role;
import com.authauz.authauz.common.UserType;

import lombok.RequiredArgsConstructor;

/**
 * UserContextBuilder is responsible for building and preparing the user context
 * that will be used throughout the application. It ensures that user-specific
 * details like userId, userType, and role are stored in the request context.
 * 
 * This context is typically used for security-related purposes and is set in
 * a virtual thread security context holder for easy access during request
 * processing. The prepared context can be accessed across the entire
 * application to support authorization and other logic based on the
 * authenticated user's details.
 * 
 * Currently, the user type and role are hardcoded for demo purposes. In a
 * real-world application, these values should be dynamically fetched from the
 * authenticated user's details (e.g., from a user database or identity
 * provider).
 */
@Component
@RequiredArgsConstructor
public class UserContextBuilder {
    public RequestContext prepareContext(UUID userId) {

        return RequestContext.builder()
                .userId(userId)
                .userType(UserType.SELLER) // Hardcoded user type for demo purposes.
                .role(Role.ADMIN) // Hardcoded role for demo purposes.
                .build();

    }

}
