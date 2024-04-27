package com.jh.accountmanagement.transaction.service;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.exception.AlreadyDeletedAccountException;
import com.jh.accountmanagement.account.exception.NotFoundAccountException;
import com.jh.accountmanagement.account.exception.NotFoundUserIdException;
import com.jh.accountmanagement.account.repository.AccountRepository;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import com.jh.accountmanagement.account.type.AccountErrorCode;
import com.jh.accountmanagement.transaction.domain.Transaction;
import com.jh.accountmanagement.transaction.dto.TransactionCancelDto;
import com.jh.accountmanagement.transaction.dto.TransactionUseDto;
import com.jh.accountmanagement.transaction.exception.NotFoundTransactionException;
import com.jh.accountmanagement.transaction.exception.NotFoundTransactionNumberException;
import com.jh.accountmanagement.transaction.exception.TransactionPriceException;
import com.jh.accountmanagement.transaction.repository.TransactionRepository;
import com.jh.accountmanagement.transaction.type.TransactionErrorCode;
import com.jh.accountmanagement.transaction.type.TransactionResult;
import com.jh.accountmanagement.transaction.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;

    /**
     * 잔액 사용
     * 사용자 없을 시 NotFoundUserIdException
     * 사용자 아이디와 계좌번호 다를 시 NotFoundAccountException
     * 계좌가 이미 해지상태일 시 AlreadyDeletedAccountException
     * 거래금액이 계좌 잔액보다 큰 경우 TransactionPriceException
     *
     * @param request 사용자 아이디, 계좌번호, 거래금액
     * @return Transaction
     */
    public Transaction transactionUse(TransactionUseDto.Request request) {
        log.info("사용자 아이디={}", request.getUserId());
        log.info("계좌번호={}", request.getAccountNum());
        log.info("거래금액={}", request.getPrice());

        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate(request.getUserId(), null).orElseThrow(() -> new NotFoundUserIdException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));
        Account account = accountRepository.findByAccountUserAndAccountNum(accountUser, request.getAccountNum()).orElseThrow(() -> new NotFoundAccountException(AccountErrorCode.NOT_FOUND_ACCOUNT.getMessage()));
        if (account.getDelDate() != null) {
            throw new AlreadyDeletedAccountException(AccountErrorCode.ALREADY_DELETED_ACCOUNT.getMessage());
        }

        if (request.getPrice() > account.getMoney()) { // 거래 금액이 계좌 잔액보다 큰 경우
            throw new TransactionPriceException(TransactionErrorCode.PRICE_MORE_THAN_ACCOUNT_MONEY.getMessage());
        }

        String randomNumber = UUID.randomUUID().toString(); // 거래 번호 uuid 생성
        String transactionNumber = createTransactionNumber(randomNumber);

        Account accountBuild = account.toBuilder() // 계좌의 잔액 수정 후 저장
                .money(account.getMoney() - request.getPrice())
                .build();
        Account modifiedAccount = accountRepository.save(accountBuild);

        Transaction transaction = Transaction.builder() // 거래 저장
                .price(request.getPrice())
                .transactionNumber(transactionNumber)
                .transactionResult(TransactionResult.S)
                .transactionType(TransactionType.TRANSACTION)
                .accountUser(accountUser)
                .account(modifiedAccount)
                .build();
        return transactionRepository.save(transaction);
    }

    /**
     * 거래 취소
     * 원거래 금액과 취소 금액이 다른 경우 exception
     * 취소하려는 거래의 계좌번호와 일치하지 않는 경우 exception
     * @param request 거래 번호, 계좌 번호, 거래 금액
     * @return Transaction
     */
    public Transaction canceledTransaction(TransactionCancelDto.Request request) {
        log.info("거래 번호={}", request.getTransactionNumber());
        log.info("계좌번호={}", request.getAccountNum());
        log.info("거래금액={}", request.getPrice());

        Transaction transaction = transactionRepository.findByTransactionNumber(request.getTransactionNumber()).orElseThrow(() -> new NotFoundTransactionNumberException(TransactionErrorCode.NOT_FOUND_TRANSACTION_NUMBER.getMessage()));
        Account account = accountRepository.findByAccountNum(request.getAccountNum()).orElseThrow(() -> new NotFoundAccountException(AccountErrorCode.NOT_FOUND_ACCOUNT.getMessage()));

        if(transaction.getPrice() != request.getPrice()){
            throw new TransactionPriceException(TransactionErrorCode.DIFF_PRICE_AND_ACCOUNT_MONEY.getMessage());
        }

        if(transaction.getAccount().getAccountNum() != request.getAccountNum()){
            throw new NotFoundTransactionException(TransactionErrorCode.NOT_FOUND_TRANSACTION_NUMBER.getMessage());
        }

        Account modifiedAccountBuild = account.toBuilder()
                .money(account.getMoney() + request.getPrice())
                .build();
        Account modifiedAccount = accountRepository.save(modifiedAccountBuild);

        String randomNum = UUID.randomUUID().toString();
        String transactionNumber = createTransactionNumber(randomNum);

        Transaction canceledTransaction = Transaction.builder()
                .transactionNumber(transactionNumber)
                .transactionResult(TransactionResult.S)
                .transactionType(TransactionType.CANCEL)
                .account(modifiedAccount)
                .accountUser(modifiedAccount.getAccountUser())
                .price(request.getPrice())
                .build();
        return transactionRepository.save(canceledTransaction);
    }

    // 거래 시 Exception 발생했을 때 실패 Transaction 저장
    public Transaction useFail(TransactionUseDto.Request request) {
        AccountUser accountUser = accountUserRepository.findByUserIdAndDelDate(request.getUserId(), null).orElseThrow(() -> new NotFoundUserIdException(AccountErrorCode.NOT_FOUNT_USER_ID.getMessage()));
        Account account = accountRepository.findByAccountUserAndAccountNum(accountUser, request.getAccountNum()).orElseThrow(() -> new NotFoundAccountException(AccountErrorCode.NOT_FOUND_ACCOUNT.getMessage()));
        if (account.getDelDate() != null) {
            throw new AlreadyDeletedAccountException(AccountErrorCode.ALREADY_DELETED_ACCOUNT.getMessage());
        }

        String randomNumber = UUID.randomUUID().toString(); // 거래 번호 uuid 생성
        String transactionNumber = createTransactionNumber(randomNumber);

        Transaction transaction = Transaction.builder() // 거래 저장
                .price(request.getPrice())
                .transactionNumber(transactionNumber)
                .transactionResult(TransactionResult.F)
                .transactionType(TransactionType.TRANSACTION)
                .accountUser(accountUser)
                .account(account)
                .build();
        return transactionRepository.save(transaction);
    }

    // 거래 취소 실패 시 exception 발생했을 때 실패 Transaction 저장
    public Transaction cancelFail(TransactionCancelDto.Request request){
        Transaction transaction = transactionRepository.findByTransactionNumber(request.getTransactionNumber()).orElseThrow(() -> new NotFoundTransactionNumberException(TransactionErrorCode.NOT_FOUND_TRANSACTION_NUMBER.getMessage()));

        String randomNum = UUID.randomUUID().toString();
        String transactionNumber = createTransactionNumber(randomNum);

        Transaction failedTransaction = Transaction.builder()
                .price(transaction.getPrice())
                .transactionResult(TransactionResult.F)
                .transactionType(TransactionType.CANCEL)
                .accountUser(transaction.getAccountUser())
                .account(transaction.getAccount())
                .transactionNumber(transactionNumber)
                .build();
        return transactionRepository.save(failedTransaction);
    }

    // 생성한 uuid 중복체크 후 거래번호로 생성
    public String createTransactionNumber(String randomNumber) {
        String transactionNumber = randomNumber;
        while (true) { // 중복 체크
            Transaction sameTransactionNumber = transactionRepository.findByTransactionNumber(transactionNumber).orElse(null);
            if (sameTransactionNumber == null) {
                break;
            }

            transactionNumber = UUID.randomUUID().toString();
        }
        return transactionNumber;
    }
}
