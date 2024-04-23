package com.jh.accountmanagement.account.repository;

import com.jh.accountmanagement.account.model.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AccountUserRepository extends JpaRepository<AccountUser, Long> {
    Optional<AccountUser> findByUserIdAndDelDate(String userId, LocalDateTime delDate);
}
