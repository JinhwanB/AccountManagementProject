package com.jh.accountmanagement.account.service;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.dto.AccountCheck;
import com.jh.accountmanagement.account.dto.AccountCreate;
import com.jh.accountmanagement.account.dto.AccountDelete;
import com.jh.accountmanagement.account.exception.*;
import com.jh.accountmanagement.account.repository.AccountRepository;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;

    /**
     * 계좌 생성
     * 사용자 없는 경우 NotFoundUserIdException
     * 계좌 10개인 경우 AccountMaximumException
     *
     * @param request 아이디, 초기 잔액
     * @return 사용자 아이디, 생성된 계좌번호, 등록일시
     */
    public Account createAccount(AccountCreate.Request request) {
        log.info("사용자 아이디={}", request.getUserId());
        log.info("초기 잔액={}", request.getInitMoney());

        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate(request.getUserId(), null).orElseThrow(() -> new NotFoundUserIdException("해당 유저를 찾을 수 없습니다."));
        List<Account> accountList = accountRepository.findAllByAccountUserAndDelDate(accountUser, null);

        if (accountList.size() == 10) {
            throw new AccountMaximumException("현재 소유하신 계좌가 10개이므로 더 이상 계좌를 생성할 수 없습니다.");
        }

        // uuid를 통해 랜덤 생성
        UUID uuid = UUID.randomUUID();
        long randomNumber;
        while (true) { // 같은 계좌 번호가 없을 때까지 생성
            randomNumber = Math.abs(uuid.getLeastSignificantBits() % 10000000000L);
            Account account = accountRepository.findByAccountNum(randomNumber).orElse(null);
            if (account == null) {
                break;
            }
        }

        return accountRepository.save(Account.builder()
                .accountNum(randomNumber)
                .accountUser(accountUser)
                .money(request.getInitMoney())
                .build());
    }

    /**
     * 계좌 해지
     * 사용자가 없는 경우 NotFoundUserIdException
     * 사용자 아이디와 계좌 소유주가 다른 경우 NotFoundAccountException
     * 계좌가 이미 해지 상태인 경우 AlreadyDeletedAccountException
     * 잔액이 있는 경우 DeleteAccountFailException
     *
     * @param request 사용자 아이디, 계좌번호
     * @return 사용자 아이디, 계좌번호, 해지 일시
     */
    public Account deleteAccount(AccountDelete.Request request) {
        log.info("사용자 아이디={}", request.getUserId());
        log.info("계좌번호={}", request.getAccountNum());

        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate(request.getUserId(), null).orElseThrow(() -> new NotFoundUserIdException("해당 유저를 찾을 수 없습니다."));
        Account account = accountRepository.findByAccountUserAndAccountNum(accountUser, request.getAccountNum()).orElseThrow(() -> new NotFoundAccountException("해당 아이디의 계좌를 찾을 수 없습니다."));
        if (account.getDelDate() != null) {
            throw new AlreadyDeletedAccountException("이미 해지된 계좌입니다.");
        }

        if (account.getMoney() != 0) {
            throw new DeleteAccountFailException("해당 계좌에 잔액이 남아있습니다.");
        }

        Account deletedAccount = account.toBuilder()
                .delDate(LocalDateTime.now())
                .build();
        return accountRepository.save(deletedAccount);
    }

    /**
     * 계좌 확인
     * 사용자가 없는 경우 NotFoundUserIdException
     *
     * @param request 사용자 아이디
     * @return (계좌번호, 잔액) 정보의 list
     */
    public List<Account> checkAccount(AccountCheck.Request request) {
        log.info("사용자 아이디={}", request.getUserId());

        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate(request.getUserId(), null).orElseThrow(() -> new NotFoundUserIdException("해당 유저를 찾을 수 없습니다."));
        return accountRepository.findAllByAccountUserAndDelDate(accountUser, null);
    }
}
