package com.jh.accountmanagement.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AccountLock {
    long tryLockTime() default 5000L;
}
