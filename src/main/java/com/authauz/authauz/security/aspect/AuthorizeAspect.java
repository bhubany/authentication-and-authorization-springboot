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

        // Skip authorization checks for system endpoints (e.g., Swagger API docs)
        if (isAuthBypassedForEndpoint()) {
            return joinPoint.proceed();
        }

        // Handle method-specific authorization based on annotations
        if (method.isAnnotationPresent(Authorize.class)) {
            Authorize authorize = method.getAnnotation(Authorize.class);

            // If the @Authorize annotation specifies bypass, skip authorization
            if (authorize.bypass()) {
                return joinPoint.proceed();
            }

            allowedScopes = List.of(authorize.scope().getValue());
        } else if (method.isAnnotationPresent(AuthorizeList.class)) {
            AuthorizeList authorizeList = method.getAnnotation(AuthorizeList.class);
            allowedScopes = Arrays.stream(authorizeList.value())
                    .map(el -> el.scope().getValue())
                    .collect(Collectors.toList());
        }

        handleAuthentication();
        handleAuthorization(allowedScopes);

        return joinPoint.proceed();
    }

    /**
     * Verifies that the user is authenticated. If not, throws a SecurityException.
     */
    private void handleAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            // Authentication is not valid, throw an exception
            throw new SecurityException("Unauthorized access: User is not authenticated");
        }
    }

    /**
     * Verifies if the authenticated user has the necessary permissions (scopes)
     * to access the requested resource. It compares the user's type and role
     * with the allowed scopes and throws an exception if authorization fails.
     * 
     * @param allowedScopes The list of scopes required for the method.
     */
    private void handleAuthorization(List<String> allowedScopes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RequestContext ctx = (RequestContext) Optional.ofNullable(authentication)
                .map(Authentication::getPrincipal)
                .orElse(null);

        if (Objects.isNull(ctx)) {
            throw new RuntimeException("Unauthorized: No user context found");
        }

        // Check if the user has the necessary permissions for any of the allowed scopes
        for (String allowedScope : allowedScopes) {
            String[] allowedScopeParts = allowedScope.split(":");

            if (allowedScopeParts[0].equalsIgnoreCase(ctx.getUserType().toString())
                    && (allowedScopeParts[1].equals("*")
                            || allowedScopeParts[1].equalsIgnoreCase(ctx.getRole().toString()))) {
                return;
            }
        }

        // If no match is found, throw an authorization failure exception
        throw new RuntimeException("Authorization failed: User " + ctx.getUserType() + " with ID: "
                + ctx.getUserId() + " attempted to access an endpoint without the necessary permissions.");
    }

    /**
     * Checks if the current endpoint is exempt from authentication based on the
     * configured list of bypassed endpoints in the application properties.
     * 
     * @return True if authentication should be bypassed for the current endpoint.
     */
    private boolean isAuthBypassedForEndpoint() {
        List<String> NO_AUTH_EP = Optional.ofNullable(properties.getAuth())
                .map(el -> properties.getAuth().getBypass())
                .map(el -> properties.getAuth().getBypass().getEndpoints())
                .orElse(List.of());

        return NO_AUTH_EP.stream()
                .anyMatch(ep -> pathMatcher.match(ep, request.getRequestURL().toString()));
    }
}
