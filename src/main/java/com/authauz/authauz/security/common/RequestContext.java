package com.authauz.authauz.security.common;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestContext {
    private UserType userType;
    private UUID userId;
    // we can add more as per our requirements
}
