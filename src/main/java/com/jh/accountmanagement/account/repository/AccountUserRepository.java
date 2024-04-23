package com.jh.accountmanagement.account.repository;

import com.jh.accountmanagement.account.model.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountUserRepository extends JpaRepository<AccountUser, Long> {
}
