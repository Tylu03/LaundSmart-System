package com.c1se22.publiclaundsmartsystem.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
   @Around("@annotation(com.c1se22.publiclaundsmartsystem.annotation.Loggable)")
   public Object logAroundServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
       String methodName = joinPoint.getSignature().getName();
       String className = joinPoint.getTarget().getClass().getSimpleName();

       log.info("Entering method [{}] in class [{}]", methodName, className);

       long startTime = System.currentTimeMillis();
       Object result = null;
       try {
           result = joinPoint.proceed();
           long endTime = System.currentTimeMillis();
           log.info("Method [{}] in class [{}] completed in {}ms", methodName, className, (endTime - startTime));
           return result;
       } catch (Exception e) {
           log.error("Exception in method [{}] in class [{}]: {}", methodName, className, e.getMessage());
           throw e;
       }
   }
} 