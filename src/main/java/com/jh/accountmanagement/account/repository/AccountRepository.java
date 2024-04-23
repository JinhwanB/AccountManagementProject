package com.jh.accountmanagement.account.repository;

import com.jh.accountmanagement.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByUserId(String userId);
}
