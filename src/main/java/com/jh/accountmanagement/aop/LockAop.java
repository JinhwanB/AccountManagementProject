package com.jh.accountmanagement.aop;

import com.jh.accountmanagement.transaction.dto.TransactionUseDto;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LockAop {
    private final LockService lockService;

    @Around("@annotation(com.jh.accountmanagement.aop.AccountLock) && args(request)")
    public Object aroundMethod(ProceedingJoinPoint joinPoint, TransactionUseDto.Request request) throws Throwable {
        lockService.lock(request.getAccountNum());
        try {
            return joinPoint.proceed();
        } finally {
            lockService.unlock(request.getAccountNum());
        }
    }
}
