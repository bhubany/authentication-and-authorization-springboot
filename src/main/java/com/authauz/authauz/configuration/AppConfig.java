package com.authauz.authauz.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "com.smaitic.tax.taxexempt")
public class AppConfig {
    private JwtConfiguration jwt;
    private CookieConfiguration cookie;
    private AuthConfiguration auth;

    @Getter
    @Setter
    public static class JwtConfiguration {
        private String secret;
        private int expiresIn;
    }

    @Getter
    @Setter
    public static class CookieConfiguration {
        private String name;
        private int expiresIn;
    }

    @Getter
    @Setter
    public static class AuthConfiguration {
        private BypassConfig bypass;

        @Getter
        @Setter
        public static class BypassConfig {
            private List<String> endpoints;

        }
    }

}