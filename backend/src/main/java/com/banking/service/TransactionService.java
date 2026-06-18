package com.banking.service;

import com.banking.cache.FrozenAccountCache;
import com.banking.dto.TransactionResponse;
import com.banking.dto.TransferRequest;
import com.banking.exception.AccountFrozenException;
import com.banking.exception.InsufficientFundsException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.model.User;
import com.banking.model.enums.TransactionType;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final FrozenAccountCache frozenAccountCache;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository,
                              FrozenAccountCache frozenAccountCache) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.frozenAccountCache = frozenAccountCache;
    }

    @Transactional
    public List<TransactionResponse> transferFunds(TransferRequest request, User user) {
        // 1. Find source and destination accounts by account number
        Account sourceAccount = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", request.getSourceAccountNumber()));
        Account destAccount = accountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", request.getDestinationAccountNumber()));

        // 2. Validate source account belongs to user
        if (!sourceAccount.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You do not own the source account");
        }

        // 3. Check frozen status for both accounts
        if (frozenAccountCache.isFrozen(sourceAccount.getId())) {
            throw new AccountFrozenException(sourceAccount.getAccountNumber());
        }
        if (frozenAccountCache.isFrozen(destAccount.getId())) {
            throw new AccountFrozenException(destAccount.getAccountNumber());
        }

        BigDecimal amount = request.getAmount();

        // 4. Pre-validate balance before acquiring locks
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(sourceAccount.getAccountNumber(), amount);
        }
        if (amount.compareTo(sourceAccount.getDailyTransactionLimit()) > 0) {
            throw new IllegalArgumentException("Transfer amount exceeds daily limit of " + sourceAccount.getDailyTransactionLimit());
        }

        // 5. Deadlock prevention: lock accounts in ascending ID order
        Account firstLock, secondLock;
        if (sourceAccount.getId() < destAccount.getId()) {
            firstLock = accountRepository.findByIdForUpdate(sourceAccount.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account", sourceAccount.getId()));
            secondLock = accountRepository.findByIdForUpdate(destAccount.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account", destAccount.getId()));
        } else {
            firstLock = accountRepository.findByIdForUpdate(destAccount.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account", destAccount.getId()));
            secondLock = accountRepository.findByIdForUpdate(sourceAccount.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account", sourceAccount.getId()));
        }

        // Assign locked accounts back to source/dest
        Account lockedSource = sourceAccount.getId().equals(firstLock.getId()) ? firstLock : secondLock;
        Account lockedDest = destAccount.getId().equals(firstLock.getId()) ? firstLock : secondLock;

        // Intentional 10-second delay to test pessimistic locking concurrency
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Transaction interrupted during lock test", e);
        }

        // 6. Re-validate balance after acquiring locks (double-check)
        if (lockedSource.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(lockedSource.getAccountNumber(), amount);
        }

        // 7. Debit source, credit destination
        lockedSource.setBalance(lockedSource.getBalance().subtract(amount));
        lockedDest.setBalance(lockedDest.getBalance().add(amount));

        // 8. Create transactions
        LocalDateTime now = LocalDateTime.now();
        String description = request.getDescription() != null ? request.getDescription() : "Transfer";

        Transaction transferOut = Transaction.builder()
                .type(TransactionType.TRANSFER_OUT)
                .amount(amount)
                .balanceAfter(lockedSource.getBalance())
                .description(description + " to " + lockedDest.getAccountNumber())
                .account(lockedSource)
                .timestamp(now)
                .build();

        Transaction transferIn = Transaction.builder()
                .type(TransactionType.TRANSFER_IN)
                .amount(amount)
                .balanceAfter(lockedDest.getBalance())
                .description(description + " from " + lockedSource.getAccountNumber())
                .account(lockedDest)
                .timestamp(now)
                .build();

        // 9. Save both accounts and both transactions
        accountRepository.save(lockedSource);
        accountRepository.save(lockedDest);
        transactionRepository.save(transferOut);
        transactionRepository.save(transferIn);

        // 10. Return both transaction responses
        return List.of(mapToResponse(transferOut), mapToResponse(transferIn));
    }

    public List<TransactionResponse> getTransactionHistory(Long accountId, User user) {
        // Validate ownership
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You do not own this account");
        }

        return transactionRepository.findByAccountIdOrderByTimestampDesc(accountId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType().name())
                .amount(transaction.getAmount())
                .balanceAfter(transaction.getBalanceAfter())
                .description(transaction.getDescription())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}
