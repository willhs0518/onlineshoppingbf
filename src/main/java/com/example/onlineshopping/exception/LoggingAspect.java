package com.example.onlineshopping.exception;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @AfterThrowing(pointcut = "execution(* com.example.onlineshopping.service.*.*(..))", throwing = "ex")
    public void logException(Exception ex) {
        System.err.println("Exception caught in AOP: " + ex.getMessage());
        ex.printStackTrace();
    }

    @Around("within(com.example.onlineshopping.controller..*)")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }
}
