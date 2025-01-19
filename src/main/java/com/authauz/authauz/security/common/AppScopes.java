package com.authauz.authauz.security.common;

import lombok.Getter;

@Getter
public enum AppScopes {
    NONE(null),
    ALL("*");

    private final String value;

    AppScopes(String value) {
        this.value = value;
    }
}
