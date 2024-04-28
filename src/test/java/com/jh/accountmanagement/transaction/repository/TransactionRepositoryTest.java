package com.jh.accountmanagement.transaction.repository;

import com.jh.accountmanagement.account.domain.Account;
import com.jh.accountmanagement.account.domain.AccountUser;
import com.jh.accountmanagement.account.repository.AccountRepository;
import com.jh.accountmanagement.account.repository.AccountUserRepository;
import com.jh.accountmanagement.account.type.AccountErrorCode;
import com.jh.accountmanagement.transaction.domain.Transaction;
import com.jh.accountmanagement.transaction.exception.NotFoundTransactionNumberException;
import com.jh.accountmanagement.transaction.exception.TransactionPriceException;
import com.jh.accountmanagement.transaction.type.TransactionErrorCode;
import com.jh.accountmanagement.transaction.type.TransactionResult;
import com.jh.accountmanagement.transaction.type.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryTest {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountUserRepository accountUserRepository;

    @Autowired
    private AccountRepository accountRepository;

    private AccountUser accountUser;
    private Account account;

    @BeforeEach
    void before() {
        AccountUser accountUserBuild = AccountUser.builder()
                .userId("test")
                .build();
        accountUserBuild.setRegDate(LocalDateTime.now());
        accountUserBuild.setChgDate(LocalDateTime.now());
        accountUser = accountUserRepository.save(accountUserBuild);

        Account accountBuild = Account.builder()
                .accountNum(3204965758L)
                .accountUser(accountUser)
                .money(3000)
                .build();
        accountBuild.setRegDate(LocalDateTime.now());
        accountBuild.setChgDate(LocalDateTime.now());
        account = accountRepository.save(accountBuild);
    }

    @Test
    @DisplayName("잔액 사용")
    void transactionUse() {
        long price = 1000;
        Account useAccount = account.toBuilder()
                .money(account.getMoney() - price)
                .build();
        useAccount.setRegDate(account.getRegDate());
        useAccount.setChgDate(LocalDateTime.now());

        Transaction transactionBuild = Transaction.builder()
                .account(useAccount)
                .accountUser(accountUser)
                .transactionNumber("12345")
                .transactionType(TransactionType.TRANSACTION)
                .transactionResult(TransactionResult.S)
                .price(price)
                .build();
        transactionBuild.setRegDate(LocalDateTime.now());
        transactionBuild.setChgDate(LocalDateTime.now());
        Transaction transaction = transactionRepository.save(transactionBuild);
        accountRepository.save(useAccount);

        Account foundAccount = accountRepository.findByAccountNum(3204965758L).orElseThrow(() -> new NotFoundAccountException(AccountErrorCode.NOT_FOUND_ACCOUNT.getMessage()));
        assertThat(transaction.getPrice()).isEqualTo(1000);
        assertThat(transaction.getAccount().getMoney()).isEqualTo(2000);
        assertThat(foundAccount.getMoney()).isEqualTo(2000);
    }

    @Test
    @DisplayName("잔액 사용 취소")
    void transactionCancel() {
        long price = 1000;
        Account useAccount = account.toBuilder()
                .money(account.getMoney() - price)
                .build();
        useAccount.setRegDate(account.getRegDate());
        useAccount.setChgDate(LocalDateTime.now());

        Transaction transactionBuild = Transaction.builder()
                .account(useAccount)
                .accountUser(accountUser)
                .transactionNumber("12345")
                .transactionType(TransactionType.TRANSACTION)
                .transactionResult(TransactionResult.S)
                .price(price)
                .build();
        transactionBuild.setRegDate(LocalDateTime.now());
        transactionBuild.setChgDate(LocalDateTime.now());
        transactionRepository.save(transactionBuild);
        accountRepository.save(useAccount);

        Account usedAccount = accountRepository.findByAccountNum(3204965758L).orElseThrow(() -> new NotFoundAccountException(AccountErrorCode.NOT_FOUND_ACCOUNT.getMessage()));
        Transaction transaction = transactionRepository.findByTransactionNumber("12345").orElseThrow(() -> new NotFoundTransactionNumberException(TransactionErrorCode.NOT_FOUND_TRANSACTION_NUMBER.getMessage()));
        Account canceledAccount = usedAccount.toBuilder()
                .money(usedAccount.getMoney() + price)
                .build();
        canceledAccount.setRegDate(usedAccount.getRegDate());
        canceledAccount.setChgDate(LocalDateTime.now());
        Transaction canceledTransaction = Transaction.builder()
                .accountUser(accountUser)
                .transactionNumber("55634")
                .transactionResult(TransactionResult.S)
                .transactionType(TransactionType.CANCEL)
                .account(canceledAccount)
                .price(price)
                .build();
        canceledTransaction.setRegDate(transaction.getRegDate());
        canceledTransaction.setChgDate(LocalDateTime.now());
        Account savedAccount = accountRepository.save(canceledAccount);
        Transaction savedTransaction = transactionRepository.save(canceledTransaction);

        assertThat(savedTransaction.getTransactionType()).isEqualTo(TransactionType.CANCEL);
        assertThat(savedTransaction.getAccount().getMoney()).isEqualTo(3000);
        assertThat(savedAccount.getMoney()).isEqualTo(3000);
    }

    @Test
    @DisplayName("거래 확인 - 거래 번호")
    void transactionCheck() {
        long price = 1000;
        Account useAccount = account.toBuilder()
                .money(account.getMoney() - price)
                .build();
        useAccount.setRegDate(account.getRegDate());
        useAccount.setChgDate(LocalDateTime.now());

        Transaction transactionBuild = Transaction.builder()
                .account(useAccount)
                .accountUser(accountUser)
                .transactionNumber("12345")
                .transactionType(TransactionType.TRANSACTION)
                .transactionResult(TransactionResult.S)
                .price(price)
                .build();
        transactionBuild.setRegDate(LocalDateTime.now());
        transactionBuild.setChgDate(LocalDateTime.now());
        transactionRepository.save(transactionBuild);
        accountRepository.save(useAccount);

        Account failAccountBuild = Account.builder()
                .accountNum(3249576829L)
                .accountUser(accountUser)
                .money(5000)
                .build();
        failAccountBuild.setRegDate(LocalDateTime.now());
        failAccountBuild.setChgDate(LocalDateTime.now());
        Account failAccount = accountRepository.save(failAccountBuild);

        Transaction failTransactionBuild = Transaction.builder()
                .transactionNumber("32325")
                .transactionResult(TransactionResult.F)
                .transactionType(TransactionType.CANCEL)
                .price(2000)
                .account(failAccount)
                .accountUser(accountUser)
                .build();
        failTransactionBuild.setRegDate(LocalDateTime.now());
        failTransactionBuild.setChgDate(LocalDateTime.now());
        transactionRepository.save(failTransactionBuild);

        Transaction failTransaction1 = transactionRepository.findByTransactionNumber("12345").orElseThrow(() -> new NotFoundTransactionNumberException(TransactionErrorCode.NOT_FOUND_TRANSACTION_NUMBER.getMessage()));
        assertThat(failTransaction1.getTransactionNumber()).isEqualTo("12345");
        assertThat(failTransaction1.getTransactionType()).isEqualTo(TransactionType.TRANSACTION);
        assertThat(failTransaction1.getTransactionResult()).isEqualTo(TransactionResult.S);

        Transaction failTransaction2 = transactionRepository.findByTransactionNumber("32325").orElseThrow(() -> new NotFoundTransactionNumberException(TransactionErrorCode.NOT_FOUND_TRANSACTION_NUMBER.getMessage()));
        assertThat(failTransaction2.getTransactionNumber()).isEqualTo("32325");
        assertThat(failTransaction2.getTransactionType()).isEqualTo(TransactionType.CANCEL);
        assertThat(failTransaction2.getTransactionResult()).isEqualTo(TransactionResult.F);
    }

    @Test
    @DisplayName("거래 확인 - 사용자 id")
    void transactionCheckByUserId() {
        Transaction transactionBuild = Transaction.builder()
                .account(account)
                .price(1000)
                .transactionType(TransactionType.TRANSACTION)
                .transactionResult(TransactionResult.S)
                .transactionNumber("12345")
                .accountUser(accountUser)
                .build();
        transactionBuild.setRegDate(LocalDateTime.now());
        transactionBuild.setChgDate(LocalDateTime.now());
        transactionRepository.save(transactionBuild);

        Transaction canceledTransactionBuild = Transaction.builder()
                .price(10000)
                .transactionType(TransactionType.CANCEL)
                .transactionResult(TransactionResult.F)
                .transactionNumber("32567")
                .accountUser(accountUser)
                .account(account)
                .build();
        canceledTransactionBuild.setRegDate(LocalDateTime.now());
        canceledTransactionBuild.setChgDate(LocalDateTime.now());
        transactionRepository.save(canceledTransactionBuild);

        List<Transaction> transactionList = transactionRepository.findByAccountUser(accountUser);
        assertThat(transactionList).hasSize(2);
        assertThat(transactionList.get(0).getTransactionType()).isEqualTo(TransactionType.TRANSACTION);
        assertThat(transactionList.get(0).getTransactionNumber()).isEqualTo("12345");
        assertThat(transactionList.get(1).getTransactionType()).isEqualTo(TransactionType.CANCEL);
        assertThat(transactionList.get(1).getTransactionNumber()).isEqualTo("32567");
    }

    @Test
    @DisplayName("거래 금액이 계좌 잔액 초과")
    void priceException() {
        long price = 10000;
        Transaction save;
        try {
            if (price > account.getMoney()) {
                throw new TransactionPriceException(TransactionErrorCode.PRICE_MORE_THAN_ACCOUNT_MONEY.getMessage());
            }

            Transaction transactionBuild = Transaction.builder()
                    .account(account)
                    .price(10000)
                    .transactionType(TransactionType.TRANSACTION)
                    .transactionResult(TransactionResult.S)
                    .transactionNumber("12345")
                    .accountUser(accountUser)
                    .build();
            transactionBuild.setRegDate(LocalDateTime.now());
            transactionBuild.setChgDate(LocalDateTime.now());
            save = transactionRepository.save(transactionBuild);
        } catch (TransactionPriceException e) {
            Transaction transactionBuild = Transaction.builder()
                    .account(account)
                    .price(10000)
                    .transactionType(TransactionType.TRANSACTION)
                    .transactionResult(TransactionResult.F)
                    .transactionNumber("12345")
                    .accountUser(accountUser)
                    .build();
            transactionBuild.setRegDate(LocalDateTime.now());
            transactionBuild.setChgDate(LocalDateTime.now());
            save = transactionRepository.save(transactionBuild);

            assertThat(e.getMessage()).isEqualTo(TransactionErrorCode.PRICE_MORE_THAN_ACCOUNT_MONEY.getMessage());
        }
        assertThat(save.getTransactionResult()).isEqualTo(TransactionResult.F);
    }

    @Test
    @DisplayName("원거래 금액과 취소금액 불일치")
    void diffPriceAndMoney() {
        int originPrice = 10000;

        Transaction transactionBuild = Transaction.builder()
                .account(account)
                .price(1000)
                .transactionType(TransactionType.TRANSACTION)
                .transactionResult(TransactionResult.S)
                .transactionNumber("12345")
                .accountUser(accountUser)
                .build();
        transactionBuild.setRegDate(LocalDateTime.now());
        transactionBuild.setChgDate(LocalDateTime.now());
        Transaction transaction = transactionRepository.save(transactionBuild);

        Transaction canceledTransaction;
        try {
            if (originPrice != transaction.getPrice()) {
                throw new TransactionPriceException(TransactionErrorCode.DIFF_PRICE_AND_ACCOUNT_MONEY.getMessage());
            }

            Transaction canceledTransactionBuild = Transaction.builder()
                    .price(originPrice)
                    .transactionType(TransactionType.CANCEL)
                    .transactionResult(TransactionResult.S)
                    .transactionNumber("32567")
                    .accountUser(accountUser)
                    .account(account)
                    .build();
            canceledTransactionBuild.setRegDate(LocalDateTime.now());
            canceledTransactionBuild.setChgDate(LocalDateTime.now());
            canceledTransaction = transactionRepository.save(canceledTransactionBuild);
        } catch (TransactionPriceException e) {
            Transaction canceledTransactionBuild = Transaction.builder()
                    .price(originPrice)
                    .transactionType(TransactionType.CANCEL)
                    .transactionResult(TransactionResult.F)
                    .transactionNumber("32567")
                    .accountUser(accountUser)
                    .account(account)
                    .build();
            canceledTransactionBuild.setRegDate(LocalDateTime.now());
            canceledTransactionBuild.setChgDate(LocalDateTime.now());
            canceledTransaction = transactionRepository.save(canceledTransactionBuild);

            assertThat(e.getMessage()).isEqualTo(TransactionErrorCode.DIFF_PRICE_AND_ACCOUNT_MONEY.getMessage());
        }
        assertThat(canceledTransaction.getTransactionResult()).isEqualTo(TransactionResult.F);
    }

    @Test
    @DisplayName("거래번호와 계좌번호 불일치")
    void diffTransactionAndAccountNum() {
        long accountNum = 3254634590L;

        Transaction transactionBuild = Transaction.builder()
                .account(account)
                .price(1000)
                .transactionType(TransactionType.TRANSACTION)
                .transactionResult(TransactionResult.S)
                .transactionNumber("12345")
                .accountUser(accountUser)
                .build();
        transactionBuild.setRegDate(LocalDateTime.now());
        transactionBuild.setChgDate(LocalDateTime.now());
        Transaction transaction = transactionRepository.save(transactionBuild);

        Transaction canceledTransaction;
        try {
            if (accountNum != transaction.getAccount().getAccountNum()) {
                throw new NotFoundTransactionNumberException(TransactionErrorCode.NOT_FOUND_TRANSACTION_NUMBER.getMessage());
            }

            Transaction canceledTransactionBuild = Transaction.builder()
                    .price(1000)
                    .transactionType(TransactionType.CANCEL)
                    .transactionResult(TransactionResult.S)
                    .transactionNumber("32567")
                    .accountUser(accountUser)
                    .account(account)
                    .build();
            canceledTransactionBuild.setRegDate(LocalDateTime.now());
            canceledTransactionBuild.setChgDate(LocalDateTime.now());
            canceledTransaction = transactionRepository.save(canceledTransactionBuild);
        } catch (NotFoundTransactionNumberException e) {
            Transaction canceledTransactionBuild = Transaction.builder()
                    .price(1000)
                    .transactionType(TransactionType.CANCEL)
                    .transactionResult(TransactionResult.F)
                    .transactionNumber("32567")
                    .accountUser(accountUser)
                    .account(account)
                    .build();
            canceledTransactionBuild.setRegDate(LocalDateTime.now());
            canceledTransactionBuild.setChgDate(LocalDateTime.now());
            canceledTransaction = transactionRepository.save(canceledTransactionBuild);

            assertThat(e.getMessage()).isEqualTo(TransactionErrorCode.NOT_FOUND_TRANSACTION_NUMBER.getMessage());
        }
        assertThat(canceledTransaction.getTransactionResult()).isEqualTo(TransactionResult.F);
    }

    @Test
    @DisplayName("거래 번호가 없는 경우")
    void transactionNumber() {
        Transaction transactionBuild = Transaction.builder()
                .account(account)
                .price(1000)
                .transactionType(TransactionType.TRANSACTION)
                .transactionResult(TransactionResult.S)
                .transactionNumber("12345")
                .accountUser(accountUser)
                .build();
        transactionBuild.setRegDate(LocalDateTime.now());
        transactionBuild.setChgDate(LocalDateTime.now());
        transactionRepository.save(transactionBuild);

        String message = "";
        try {
            transactionRepository.findByTransactionNumber("32456").orElseThrow(() -> new NotFoundTransactionNumberException(TransactionErrorCode.NOT_FOUND_TRANSACTION_NUMBER.getMessage()));
        } catch (NotFoundTransactionNumberException e) {
            message = e.getMessage();
        }

        assertThat(message).isEqualTo(TransactionErrorCode.NOT_FOUND_TRANSACTION_NUMBER.getMessage());
    }
}