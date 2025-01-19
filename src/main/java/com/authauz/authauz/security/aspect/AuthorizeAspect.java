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

import com.authauz.authauz.common.RequestContext;
import com.authauz.authauz.configuration.AppConfigurationProperties;
import com.authauz.authauz.security.annotation.Authorize;
import com.authauz.authauz.security.annotation.AuthorizeList;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Aspect responsible for managing authorization checks based on annotations
 * and user roles. It intercepts method calls annotated with
 * @Authorize(s) to verify if the user has the appropriate scope and
 * permissions.
 * 
 * The aspect ensures that user authentication and authorization
 * are handled before proceeding with the actual method invocation.
 */

@Component
@Aspect
@RequiredArgsConstructor
public class AuthorizeAspect {
    private final HttpServletRequest request;
    private final AppConfigurationProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Pointcut that matches methods annotated with @Authorize or methods within
     * any class annotated with @RestController, ensuring authorization is applied
     * to RESTful endpoints.
     */

    @Pointcut("@annotation(com.authauz.authauz.security.annotation.Authorize) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void authorizationPointcut() {
    }

    /**
     * Around advice that intercepts method execution and performs authentication
     * and authorization checks before allowing the method to proceed.
     * 
     * If the method is annotated with @Authorize or @AuthorizeList, it checks if
     * the user has the required scopes. If authentication or authorization fails,
     * it throws an exception.
     * 
     * @param joinPoint The join point representing the method execution.
     * @return The result of the method execution if authorization is successful.
     * @throws Throwable If an error occurs during method execution or
     *                   authorization.
     */
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
                            || allowedScopeParts[1].equalsIgnoreCase(ctx.getRole().toString()))) {
                return;
            }

        }
        throw new RuntimeException(
                "Authorization failed: {0} with ID: {1} attempted to perform an action on {2} api without the necessary permissions"
                        + ctx.getUserType() + ctx.getUserId());
    }

    private boolean isAuthBypassedForEndpoint() {
        List<String> NO_AUTH_EP = Optional.ofNullable(properties.getAuth())
                .map(el -> properties.getAuth().getBypass())
                .map(el -> properties.getAuth().getBypass().getEndpoints()).orElse(List.of());

        return NO_AUTH_EP.stream()
                .anyMatch(ep -> pathMatcher.match(ep, request.getRequestURL().toString()));
    }
}
