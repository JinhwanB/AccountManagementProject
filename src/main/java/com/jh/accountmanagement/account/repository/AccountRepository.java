package com.jh.accountmanagement.account.repository;

import com.jh.accountmanagement.account.model.Account;
import com.jh.accountmanagement.account.model.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByAccountUserAndDelDate(AccountUser accountUser, LocalDateTime delDate);

    Optional<Account> findByAccountNumAndDelDate(long accountNum, LocalDateTime delDate);
}
