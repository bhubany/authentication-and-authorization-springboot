package com.authauz.authauz.common;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestContext {
    private UUID userId;
    private UserType userType;
    private Role role;
    // we can add more as per our requirements
}
