package com.imooc.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLogAspect {
    public static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Around("execution(* com.imooc.service.impl..*.*(..))")
    public Object recordTime(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("========= 开始执行 {}.{} ========",joinPoint.getTarget().getClass(),joinPoint.getSignature().getName());

        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();

        long takeTime = endTime - startTime;

        if (takeTime > 3000){
            logger.error("执行结束，花费时长{}毫秒",takeTime);
        }else if (takeTime >2000){
            logger.warn("执行结束，耗费时间{}毫秒",takeTime);
        }else {
            logger.info("执行结束，耗费时间{}毫秒",takeTime);
        }
        return result;
    }
}
