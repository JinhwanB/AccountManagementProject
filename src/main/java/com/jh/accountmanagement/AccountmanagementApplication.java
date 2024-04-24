package com.jh.accountmanagement;

import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    void init() {
        AccountUser test = AccountUser.builder()
                .userId("test")
                .build();
        accountUserRepository.save(test);
    }
}
