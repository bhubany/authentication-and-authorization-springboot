package com.authauz.authauz.common;

import lombok.Getter;

@Getter
public enum AppScopes {
    NONE(null),
    CUSTOMER_ALL("customer:*"),
    SELLER_ALL("seller:*"),
    SELLER_ADMIN("seller:admin"),
    SELLER_CSR("seller:csr"),
    SELLER_MARKETING("seller:marketing");

    private final String value;

    AppScopes(String value) {
        this.value = value;
    }
}
