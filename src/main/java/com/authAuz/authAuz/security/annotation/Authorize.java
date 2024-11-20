package com.authAuz.authAuz.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.authAuz.authAuz.security.common.AppScopes;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {
    AppScopes scope() default AppScopes.NONE;

    boolean bypass() default false;
}
