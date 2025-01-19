package com.authauz.authauz.security.annotation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Component
public class AuthorizeAnnotationBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();

        if (clazz.isAnnotationPresent(RestController.class)) {
            for (Method method : clazz.getDeclaredMethods()) {

                if (method.isAnnotationPresent(AuthorizeList.class)) {
                    AuthorizeList authorizeList = method.getAnnotation(AuthorizeList.class);
                    validateAuthorizeList(authorizeList, method.getName(), beanName);

                } else if (method.isAnnotationPresent(Authorize.class)) {
                    Authorize authorize = method.getAnnotation(Authorize.class);
                    validateAuthorizeArgs(Objects.nonNull(authorize.scope().getValue()), authorize.bypass(),
                            method.getName(), beanName);
                }
            }
        }
        return bean;
    }

    private void validateAuthorizeList(AuthorizeList authorizeList, String methodName, String beanName) {
        boolean containScope = Arrays.stream(authorizeList.value()).map(Authorize::scope).anyMatch(Objects::nonNull);
        boolean containBypass = Arrays.stream(authorizeList.value()).anyMatch(Authorize::bypass);

        validateAuthorizeArgs(containScope, containBypass, methodName, beanName);
    }

    private void validateAuthorizeArgs(boolean containScope, boolean containBypass, String methodName,
            String beanName) {

        if (containScope && containBypass) {
            throw new IllegalStateException(
                    "Setting both 'scopes' and 'bypass' on method " + methodName + " in bean "
                            + beanName + " is not allowed for 'Authorize' annotation");
        } else if (!containScope && !containBypass) {
            throw new IllegalStateException(
                    "Arguments value for either 'scopes' or 'bypass' on method " + methodName + " in bean "
                            + beanName + " is required  for 'Authorize' annotation");
        }
    }

}