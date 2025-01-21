package com.authauz.authauz.security.annotation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

/**
 * A BeanPostProcessor that validates the usage of custom authorization
 * annotations
 * {@link Authorize} and {@link AuthorizeList} during application startup.
 * Ensures that methods in beans annotated with {@link RestController} use these
 * annotations correctly by following the validation rules.
 */
@Component
public class AuthorizeAnnotationBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();

        // Check if the bean is a REST controller
        if (beanClass.isAnnotationPresent(RestController.class)) {
            validateAnnotationsOnMethods(beanClass, beanName);
        }

        return bean;
    }

    /**
     * Validates the {@link Authorize} and {@link AuthorizeList} annotations on all
     * declared methods of the provided bean class.
     *
     * @param beanClass the class of the bean
     * @param beanName  the name of the bean
     */
    private void validateAnnotationsOnMethods(Class<?> beanClass, String beanName) {
        for (Method method : beanClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AuthorizeList.class)) {
                validateAuthorizeList(method.getAnnotation(AuthorizeList.class), method.getName(), beanName);
            } else if (method.isAnnotationPresent(Authorize.class)) {
                validateAuthorize(method.getAnnotation(Authorize.class), method.getName(), beanName);
            }
        }
    }

    /**
     * Validates the {@link AuthorizeList} annotation by ensuring that it does not
     * contain both `scope` and `bypass` in its list of annotations.
     *
     * @param authorizeList the `@AuthorizeList` annotation to validate
     * @param methodName    the name of the method on which the annotation is
     *                      present
     * @param beanName      the name of the bean containing the method
     */
    private void validateAuthorizeList(AuthorizeList authorizeList, String methodName, String beanName) {
        boolean containsScope = Arrays.stream(authorizeList.value())
                .map(Authorize::scope)
                .anyMatch(Objects::nonNull);
        boolean containsBypass = Arrays.stream(authorizeList.value())
                .anyMatch(Authorize::bypass);

        validateAnnotationArguments(containsScope, containsBypass, methodName, beanName);
    }

    /**
     * Validates the {@link Authorize} annotation by ensuring that it does not
     * contain both `scope` and `bypass` arguments or neither of them.
     *
     * @param authorize  the `@Authorize` annotation to validate
     * @param methodName the name of the method on which the annotation is present
     * @param beanName   the name of the bean containing the method
     */
    private void validateAuthorize(Authorize authorize, String methodName, String beanName) {
        boolean containsScope = Objects.nonNull(authorize.scope().getValue());
        boolean containsBypass = authorize.bypass();

        validateAnnotationArguments(containsScope, containsBypass, methodName, beanName);
    }

    /**
     * Validates the combination of `scope` and `bypass` arguments for both
     * {@link Authorize} and {@link AuthorizeList} annotations. Throws an exception
     * if the validation rules are violated.
     *
     * @param containsScope  whether the annotation contains a `scope` value
     * @param containsBypass whether the annotation has `bypass` set to true
     * @param methodName     the name of the method being validated
     * @param beanName       the name of the bean containing the method
     */
    private void validateAnnotationArguments(boolean containsScope, boolean containsBypass,
            String methodName, String beanName) {
        if (containsScope && containsBypass) {
            throw new IllegalStateException(String.format(
                    "The method '%s' in bean '%s' cannot have both 'scope' and 'bypass' set "
                            + "simultaneously in the '@Authorize' annotation.",
                    methodName, beanName));
        }
        if (!containsScope && !containsBypass) {
            throw new IllegalStateException(String.format(
                    "The method '%s' in bean '%s' must have either 'scope' or 'bypass' set "
                            + "in the '@Authorize' annotation.",
                    methodName, beanName));
        }
    }

}