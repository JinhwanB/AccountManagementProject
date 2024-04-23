package com.jh.accountmanagement.account.repository;

import com.jh.accountmanagement.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumAndDelDate(long accountNum, LocalDateTime delDate);
}
