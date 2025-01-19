package com.authauz.authauz.security.aspect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.authauz.authauz.configuration.AppConfig;
import com.authauz.authauz.security.annotation.Authorize;
import com.authauz.authauz.security.annotation.AuthorizeList;
import com.authauz.authauz.security.common.RequestContext;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@Aspect
@RequiredArgsConstructor
public class AuthorizeAspect {
    private final HttpServletRequest request;
    private final AppConfig appConfig;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Pointcut("@annotation(com.smaitic.tax.taxexempt.security.annotation.Authorize) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void authorizationPointcut() {
    }

    @Around("authorizationPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        List<String> allowedScopes = List.of();

        // bypass systmem configured apis calls like swagger api docs
        if (isAuthBypassedForEndpoint()) {
            return joinPoint.proceed();
        }

        if (method.isAnnotationPresent(Authorize.class)) {
            Authorize authorize = method.getAnnotation(Authorize.class);

            if (authorize.bypass()) {
                return joinPoint.proceed();
            }

            allowedScopes = List.of(authorize.scope().getValue());
        } else if (method.isAnnotationPresent(AuthorizeList.class)) {
            AuthorizeList authorizeList = method.getAnnotation(AuthorizeList.class);
            allowedScopes = Arrays.stream(authorizeList.value()).map(el -> el.scope().getValue())
                    .collect(Collectors.toList());
        }

        handleAuthentication();
        handleAuthorization(allowedScopes);
        return joinPoint.proceed();

    }

    private void handleAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            // throw new Unauthorized("unauthorized");
            throw new SecurityException("unauthorized");
        }
    }

    private void handleAuthorization(List<String> allowedScopes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RequestContext ctx = ((RequestContext) Optional.ofNullable(authentication).map(Authentication::getPrincipal)
                .orElse(null));

        if (Objects.isNull(ctx)) {
            throw new RuntimeException("unauthorized");
        }

        for (String allowedScope : allowedScopes) {
            String[] allowedScopeParts = allowedScope.split(":");

            if (allowedScopeParts[0].equalsIgnoreCase(ctx.getUserType().toString())
                    && (allowedScopeParts[1].equals("*")
                            || allowedScopeParts[1].equalsIgnoreCase(ctx.getUserType().toString()))) {
                return;
            }

        }
        throw new RuntimeException(
                "Authorization failed: {0} with ID: {1} attempted to perform an action on {2} api without the necessary permissions"
                        + ctx.getUserType() + ctx.getUserId());
    }

    private boolean isAuthBypassedForEndpoint() {
        // List<String> NO_AUTH_EP = Optional.ofNullable(appConfig.getAuth())
        // .map(TaxExemptAppConfig.AuthConfiguration::getBypass)
        // .map(TaxExemptAppConfig.AuthConfiguration.BypassConfig::getEndpoints).orElse(List.of());
        // return NO_AUTH_EP.stream()
        // .anyMatch(ep -> pathMatcher.match(ep, request.getRequestURL().toString()));

        return false;
    }
}
