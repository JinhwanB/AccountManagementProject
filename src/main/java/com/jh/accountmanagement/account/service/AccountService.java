package com.jh.accountmanagement.account.service;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.dto.AccountCheckDto;
import com.jh.accountmanagement.account.dto.AccountCreateDto;
import com.jh.accountmanagement.account.dto.AccountDeleteDto;
import com.jh.accountmanagement.account.exception.AccountException;
import com.jh.accountmanagement.account.repository.AccountRepository;
import com.jh.accountmanagement.account.type.AccountErrorCode;
import com.jh.accountmanagement.config.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountUserService accountUserService;
    private final RedisUtils redisUtils;

    /**
     * 계좌 생성
     * 사용자 없는 경우 NotFoundUserIdException
     * 계좌 10개인 경우 AccountMaximumException
     *
     * @param request 아이디, 초기 잔액
     * @return 사용자 아이디, 생성된 계좌번호, 등록일시
     */
    public Account createAccount(AccountCreateDto.Request request) {
        log.info("사용자 아이디={}", request.getUserId());
        log.info("초기 잔액={}", request.getInitMoney());

        AccountUser accountUser = accountUserService.getUser(request.getUserId());
        List<Account> accountList = accountRepository.findAllByAccountUserAndDelDate(accountUser, null);

        if (accountList.size() == 10) {
            throw new AccountException(AccountErrorCode.ACCOUNT_MAXIMUM.getMessage());
        }

        // uuid를 통해 랜덤 생성
        UUID uuid = UUID.randomUUID();
        String randomNumber;
        while (true) { // 같은 계좌 번호가 없을 때까지 생성
            randomNumber = String.valueOf(Math.abs(uuid.getLeastSignificantBits() % 10000000000L));
            Account account;
            try {
                account = getAccount(randomNumber);
            } catch (AccountException e) {
                account = null;
            }

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
    public Account deleteAccount(AccountDeleteDto.Request request) {
        log.info("사용자 아이디={}", request.getUserId());
        log.info("계좌번호={}", request.getAccountNum());

        AccountUser accountUser = accountUserService.getUser(request.getUserId());
        Account account = getAccount(request.getAccountNum());
        if (!account.getAccountUser().equals(accountUser)) {
            throw new AccountException(AccountErrorCode.DIFF_USER_AND_ACCOUNT_NUMBER.getMessage());
        }

        if (account.getDelDate() != null) {
            throw new AccountException(AccountErrorCode.ALREADY_DELETED_ACCOUNT.getMessage());
        }

        if (account.getMoney() != 0) {
            throw new AccountException(AccountErrorCode.DELETE_ACCOUNT_FAIL.getMessage());
        }

        Account deletedAccount = account.toBuilder()
                .delDate(LocalDateTime.now())
                .build();
        redisUtils.delete(deletedAccount.getAccountNum());
        return accountRepository.save(deletedAccount);
    }

    /**
     * 계좌 확인
     * 사용자가 없는 경우 NotFoundUserIdException
     *
     * @param request 사용자 아이디
     * @return (계좌번호, 잔액) 정보의 list
     */
    public List<Account> checkAccount(AccountCheckDto.Request request) {
        log.info("사용자 아이디={}", request.getUserId());

        AccountUser accountUser = accountUserService.getUser(request.getUserId());
        return accountRepository.findAllByAccountUserAndDelDate(accountUser, null);
    }

    public Account getAccount(String accountNum) {
        log.info("계좌 번호={}", accountNum);

        if (redisUtils.hasKey(accountNum)) {
            return (Account) redisUtils.get(accountNum);
        }

        Account account = accountRepository.findByAccountNum(accountNum).orElseThrow(() -> new AccountException(AccountErrorCode.NOT_FOUND_ACCOUNT.getMessage()));
        if (account.getDelDate() != null) {
            throw new AccountException(AccountErrorCode.ALREADY_DELETED_ACCOUNT.getMessage());
        }
        redisUtils.set(accountNum, account);
        return account;
    }
}
