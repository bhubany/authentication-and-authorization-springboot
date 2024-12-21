package com.authAuz.authAuz.security.provider;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.authAuz.authAuz.security.common.RequestContext;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserContextBuilder {

    public RequestContext prepareContext(UUID authId, UUID sellerLocationId) {
        return RequestContext.builder()
                .build();

    }

}
