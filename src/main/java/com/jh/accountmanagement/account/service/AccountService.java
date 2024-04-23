package com.jh.accountmanagement.account.service;

import com.jh.accountmanagement.account.dto.AccountCreate;
import com.jh.accountmanagement.account.exception.AccountMaximumException;
import com.jh.accountmanagement.account.exception.NotFoundUserIdException;
import com.jh.accountmanagement.account.model.Account;
import com.jh.accountmanagement.account.model.AccountUser;
import com.jh.accountmanagement.account.repository.AccountRepository;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;

    public AccountCreate.Response createAccount(AccountCreate.Request request) {
        log.info("사용자 아이디={}", request.getUserId());
        log.info("초기 잔액={}", request.getInitMoney());

        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate(request.getUserId(), null).orElseThrow(() -> new NotFoundUserIdException("해당 유저를 찾을 수 없습니다."));
        List<Account> accountList = accountUser.getAccountList().stream().filter(x -> x.getDelDate() == null).toList();

        if (accountList.size() == 10) {
            throw new AccountMaximumException("현재 소유하신 계좌가 10개이므로 더 이상 계좌를 생성할 수 없습니다.");
        }

        UUID uuid = UUID.randomUUID();
        long randomNumber;
        while (true) {
            randomNumber = Math.abs(uuid.getLeastSignificantBits() % 10000000000L);
            long finalRandomNumber = randomNumber;
            Account account = accountList.stream().filter(o -> o.getAccountNum() == finalRandomNumber).findFirst().orElse(null);
            if (account == null) {
                break;
            }
        }

        Account build = Account.builder()
                .accountNum(randomNumber)
                .accountUser(accountUser)
                .money(request.getInitMoney())
                .build();
        accountRepository.save(build);
        return build.toResponse();
    }
}
