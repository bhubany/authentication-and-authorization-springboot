package com.authauz.authauz.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.authauz.authauz.common.AppScopes;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AuthorizeList.class)
public @interface Authorize {
    AppScopes scope() default AppScopes.NONE;

    boolean bypass() default false;
}
