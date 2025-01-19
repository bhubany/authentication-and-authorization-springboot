package com.authauz.authauz.security.provider;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.authauz.authauz.security.common.RequestContext;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserContextBuilder {

    public RequestContext prepareContext(UUID authId, UUID sellerLocationId) {
        return RequestContext.builder()
                .build();

    }

}
