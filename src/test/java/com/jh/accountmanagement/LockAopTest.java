package com.jh.accountmanagement;

import com.jh.accountmanagement.account.exception.AccountException;
import com.jh.accountmanagement.account.type.AccountErrorCode;
import com.jh.accountmanagement.aop.LockAop;
import com.jh.accountmanagement.aop.LockService;
import com.jh.accountmanagement.transaction.dto.TransactionUseDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LockAopTest {
    @Mock
    private LockService lockService;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @InjectMocks
    private LockAop lockAop;

    @Test
    void lockAndUnLock() throws Throwable {
        // given
        ArgumentCaptor<String> lockArgumentCaptor =
                ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unLockArgumentCaptor =
                ArgumentCaptor.forClass(String.class);
        TransactionUseDto.Request request = TransactionUseDto.Request.builder()
                .userId("test")
                .price(10000)
                .accountNum("1234")
                .build();

        // when
        lockAop.aroundMethod(proceedingJoinPoint, request);

        // then
        verify(lockService, times(1)).lock(lockArgumentCaptor.capture());
        verify(lockService, times(1)).unlock(unLockArgumentCaptor.capture());
        assertEquals("1234", lockArgumentCaptor.getValue());
        assertEquals("1234", unLockArgumentCaptor.getValue());
    }

    @Test
    void lockAndUnLock_evenIfThrow() throws Throwable {
        // given
        ArgumentCaptor<String> lockArgumentCaptor =
                ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unLockArgumentCaptor =
                ArgumentCaptor.forClass(String.class);
        TransactionUseDto.Request request = TransactionUseDto.Request.builder()
                .accountNum("54321")
                .price(1000)
                .userId("test")
                .build();

        given(proceedingJoinPoint.proceed())
                .willThrow(new AccountException(AccountErrorCode.NOT_FOUND_ACCOUNT.getMessage()));

        // when
        assertThrows(AccountException.class, () ->
                lockAop.aroundMethod(proceedingJoinPoint, request));

        // then
        verify(lockService, times(1)).lock(lockArgumentCaptor.capture());
        verify(lockService, times(1)).unlock(unLockArgumentCaptor.capture());
        assertEquals("54321", lockArgumentCaptor.getValue());
        assertEquals("54321", unLockArgumentCaptor.getValue());
    }
}
