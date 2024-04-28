package com.jh.accountmanagement;

import com.jh.accountmanagement.account.repository.AccountUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class AccountmanagementApplication {
    private final AccountUserRepository accountUserRepository;

    public static void main(String[] args) {
        SpringApplication.run(AccountmanagementApplication.class, args);
    }
}
