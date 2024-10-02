package org.tbank.fintech.executor_timer_starter.postprocessor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.tbank.fintech.executor_timer_starter.execution_timer.ExecutionTimer;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ExecutionTimerPostProcessor implements BeanPostProcessor, PriorityOrdered {
    public static String LOGGING_LEVEL;
    private final Map<String, Set<Method>> annotatedBeans = new HashMap<>();
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(ExecutionTimer.class)) {
            annotatedBeans.put(beanName, Arrays.stream(beanClass.getDeclaredMethods()).collect(Collectors.toSet()));
        } else {
            for (Method method : beanClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(ExecutionTimer.class)) {
                    if (annotatedBeans.containsKey(beanName)) {
                        annotatedBeans.get(beanName).add(method);
                    } else {
                        Set<Method> set = new HashSet<>();
                        set.add(method);
                        annotatedBeans.put(beanName, set);
                    }
                }
            }
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (annotatedBeans.containsKey(beanName)) {
            ProxyFactory proxy = new ProxyFactory(bean);
            proxy.addAdvice( new ExecutionTimerInterceptor(annotatedBeans.get(beanName)));
            bean = proxy.getProxy();
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private record ExecutionTimerInterceptor(Set<Method> methods) implements MethodInterceptor {

        @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                Method method = invocation.getMethod();
                Class<?> targetClass = Objects.requireNonNull(invocation.getThis()).getClass();
                Method targetMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());
                if (methods.contains(targetMethod)) {
                    Logger logger = LoggerFactory.getLogger(targetMethod.getDeclaringClass());
                    long startTime = System.currentTimeMillis();
                    Object obj = invocation.proceed();
                    long endTime = System.currentTimeMillis();
                    logger.atLevel(Level.valueOf(LOGGING_LEVEL.toUpperCase())).log(
                            "Method with name=" + targetMethod.getName() +
                                    " of " + targetMethod.getDeclaringClass().getName() + " class " +
                                    "worked for " + (endTime - startTime) + " milliseconds"
                    );
                    return obj;
                } else {
                    return invocation.proceed();
                }
            }
        }

    @Override
    public int getOrder() {
        return PriorityOrdered.LOWEST_PRECEDENCE;
    }

}
