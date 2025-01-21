package com.authauz.authauz.common;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents the context of a request, encapsulating essential details
 * such as the user's identity, type, and role.
 * Additional fields can be added as per future requirements.
 */
@Getter
@Builder
public class RequestContext {
    private UUID userId;
    private UserType userType;
    private Role role;
}
