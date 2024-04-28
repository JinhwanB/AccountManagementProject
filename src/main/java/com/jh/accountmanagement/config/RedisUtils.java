package com.jh.accountmanagement.config;

import com.jh.accountmanagement.account.dto.AccountRedisDto;
import com.jh.accountmanagement.transaction.dto.TransactionRedisDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtils {
    private final RedisTemplate<String, AccountRedisDto> accountRedisTemplate;
    private final RedisTemplate<String, TransactionRedisDto> transactionRedisTemplate;

    public void setAccount(String key, AccountRedisDto o) {
        ListOperations<String, AccountRedisDto> accountList = accountRedisTemplate.opsForList();
        accountList.rightPush(key, o);
        accountRedisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }

    public void setTransaction(String key, TransactionRedisDto o) {
        transactionRedisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(o.getClass()));
        transactionRedisTemplate.opsForValue().set(key, o, 30, TimeUnit.MINUTES);
    }


    public List<AccountRedisDto> getAccount(String key) {
        ListOperations<String, AccountRedisDto> accountList = accountRedisTemplate.opsForList();
        return accountList.range(key, 0, -1);
    }

    public TransactionRedisDto getTransaction(String key) {
        return transactionRedisTemplate.opsForValue().get(key);
    }

    public boolean hasKeyOfAccount(String key) {
        return Boolean.TRUE.equals(accountRedisTemplate.hasKey(key));
    }

    public boolean hasKeyOfTransaction(String key) {
        return Boolean.TRUE.equals(transactionRedisTemplate.hasKey(key));
    }

    public void deleteAccount(String key) {
        if (hasKeyOfAccount(key)) {
            accountRedisTemplate.delete(key);
        }
    }

    public void deleteTransaction(String key) {
        if (hasKeyOfTransaction(key)) {
            transactionRedisTemplate.delete(key);
        }
    }

    public void updateTransaction(String key, TransactionRedisDto o) {
        if (hasKeyOfTransaction(key)) {
            setTransaction(key, o);
        }
    }
}
