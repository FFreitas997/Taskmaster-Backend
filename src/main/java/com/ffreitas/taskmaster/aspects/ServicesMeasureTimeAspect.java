package com.ffreitas.taskmaster.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Aspect
@Component
public class ServicesMeasureTimeAspect {

    @Around("execution(* com.ffreitas.taskmaster.service.*.*(..))")
    public Object aroundMeasureTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("Service has just started: {}", proceedingJoinPoint.getSignature());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        log.info("User Authenticated: {}", auth.getName());

        log.info("Start measuring the time ...");

        Instant start = Instant.now();

        Object result;

        try {
            result = proceedingJoinPoint.proceed();
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage());
            throw e;
        } finally {
            Instant end = Instant.now();
            log.info("End measuring the time ...");
            log.info("Duration: {} milliseconds", Duration.between(start, end).toMillis());
        }

        return result;
    }
}
