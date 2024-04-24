package com.jh.accountmanagement.account.repository;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountUser(AccountUser accountUser);

    Optional<Account> findByAccountUserAndAccountNum(AccountUser accountUser, long accountNum);

    List<Account> findAllByAccountUserAndDelDate(AccountUser accountUser, LocalDateTime delDate);
}
