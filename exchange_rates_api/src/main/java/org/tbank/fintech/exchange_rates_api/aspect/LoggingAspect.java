package org.tbank.fintech.exchange_rates_api.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    @Value("${logging.level.project.clients:debug}")
    private String clientsLoggingLevel;

    @Value("${logging.level.project.exceptions:warn}")
    private String exceptionsLoggingLevel;

    @Value("${logging.level.project.services:debug}")
    private String servicesLoggingLevel;

    @Pointcut("within(org.tbank.fintech.exchange_rates_api.client.impl.*)")
    public void clientsPointcut() {}
    @Pointcut("execution(* org.tbank.fintech.exchange_rates_api.controller.CurrencyRestController.handle*(..))")
    public void exceptionsPointcut() {}
    @Pointcut("within(org.tbank.fintech.exchange_rates_api.service.impl.*)")
    public void servicesPointcut() {}

    @Around("clientsPointcut()")
    public Object clientsAroundLoggingAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        logger.atLevel(Level.valueOf(clientsLoggingLevel.toUpperCase())).log(joinPoint.toShortString() + " with args " + Arrays.toString(args));
        Object obj = joinPoint.proceed();
        logger.atLevel(Level.valueOf(clientsLoggingLevel.toUpperCase())).log("Objects obtained from the api " + obj);
        return obj;
    }

    @Before("exceptionsPointcut()")
    public void exceptionsBeforeLoggingAdvice(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Logger logger = LoggerFactory.getLogger(joinPoint.getThis().getClass());
        logger.atLevel(Level.valueOf(exceptionsLoggingLevel.toUpperCase())).log(joinPoint.toShortString() + " with args " + Arrays.toString(args));
    }

    @Around("servicesPointcut()")
    public Object servicesAroundLoggingAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Logger logger = LoggerFactory.getLogger(joinPoint.getThis().getClass());
        logger.atLevel(Level.valueOf(servicesLoggingLevel.toUpperCase())).log(joinPoint.toShortString() + " with args " + Arrays.toString(args));
        Object obj = joinPoint.proceed();
        logger.atLevel(Level.valueOf(servicesLoggingLevel.toUpperCase())).log("Service method:"+ joinPoint.getSignature().getName() + " return: " + obj);
        return obj;
    }
}
