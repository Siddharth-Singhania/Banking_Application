package com.banking.service;

import com.banking.cache.FrozenAccountCache;
import com.banking.dto.AccountRequest;
import com.banking.dto.AccountResponse;
import com.banking.dto.DepositWithdrawRequest;
import com.banking.dto.LimitUpdateRequest;
import com.banking.exception.AccountFrozenException;
import com.banking.exception.InsufficientFundsException;
import com.banking.exception.OverdraftLimitExceededException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.model.User;
import com.banking.model.enums.AccountStatus;
import com.banking.model.enums.TransactionType;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final FrozenAccountCache frozenAccountCache;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository,
                          FrozenAccountCache frozenAccountCache) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.frozenAccountCache = frozenAccountCache;
    }

    @Transactional
    public AccountResponse createAccount(AccountRequest request, User user) {
        Account account = Account.builder()
                .accountNumber(UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase())
                .accountHolderName(request.getAccountHolderName())
                .balance(BigDecimal.ZERO)
                .dailyTransactionLimit(new BigDecimal("50000.00"))
                .status(AccountStatus.ACTIVE)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        Account saved = accountRepository.save(account);
        return mapToResponse(saved);
    }

    public List<AccountResponse> getAccountsByUser(Long userId) {
        return accountRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        return mapToResponse(account);
    }

    public Account getAccountEntityById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
    }

    @Transactional
    public AccountResponse deposit(Long accountId, DepositWithdrawRequest request, User user) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        validateOwnership(account, user);

        try {
            Thread.sleep(10000); // 10-second delay for testing locks
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (frozenAccountCache.isFrozen(accountId)) {
            throw new AccountFrozenException(account.getAccountNumber());
        }

        account.setBalance(account.getBalance().add(request.getAmount()));

        Transaction transaction = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .balanceAfter(account.getBalance())
                .description(request.getDescription())
                .account(account)
                .timestamp(LocalDateTime.now())
                .build();

        accountRepository.save(account);
        transactionRepository.save(transaction);

        return mapToResponse(account);
    }

    @Transactional
    public AccountResponse withdraw(Long accountId, DepositWithdrawRequest request, User user) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        validateOwnership(account, user);

        try {
            Thread.sleep(10000); // 10-second delay for testing locks
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (frozenAccountCache.isFrozen(accountId)) {
            throw new AccountFrozenException(account.getAccountNumber());
        }

        BigDecimal amount = request.getAmount();

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(account.getAccountNumber(), amount);
        }

        if (amount.compareTo(account.getDailyTransactionLimit()) > 0) {
            throw new IllegalArgumentException("Transaction amount exceeds daily limit of " + account.getDailyTransactionLimit());
        }

        account.setBalance(account.getBalance().subtract(amount));

        Transaction transaction = Transaction.builder()
                .type(TransactionType.WITHDRAWAL)
                .amount(amount)
                .balanceAfter(account.getBalance())
                .description(request.getDescription())
                .account(account)
                .timestamp(LocalDateTime.now())
                .build();

        accountRepository.save(account);
        transactionRepository.save(transaction);

        return mapToResponse(account);
    }

    @Transactional
    public AccountResponse updateDailyLimit(Long accountId, LimitUpdateRequest request, User user) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        validateOwnership(account, user);

        account.setDailyTransactionLimit(request.getLimit());
        accountRepository.save(account);

        return mapToResponse(account);
    }

    private void validateOwnership(Account account, User user) {
        if (!account.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You do not own this account");
        }
    }

    public AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountHolderName(account.getAccountHolderName())
                .balance(account.getBalance())
                .dailyTransactionLimit(account.getDailyTransactionLimit())
                .status(account.getStatus().name())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
