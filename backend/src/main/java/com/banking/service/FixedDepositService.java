package com.banking.service;

import com.banking.dto.FixedDepositRequest;
import com.banking.dto.FixedDepositResponse;
import com.banking.exception.InsufficientFundsException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.model.Account;
import com.banking.model.FixedDeposit;
import com.banking.model.Transaction;
import com.banking.model.User;
import com.banking.model.enums.FixedDepositStatus;
import com.banking.model.enums.TransactionType;
import com.banking.repository.AccountRepository;
import com.banking.repository.FixedDepositRepository;
import com.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FixedDepositService {

    private final FixedDepositRepository fixedDepositRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public FixedDepositService(FixedDepositRepository fixedDepositRepository,
                                AccountRepository accountRepository,
                                TransactionRepository transactionRepository) {
        this.fixedDepositRepository = fixedDepositRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public FixedDepositResponse createFixedDeposit(Long accountId, FixedDepositRequest request, User user) {
        if (!request.isAgreedToTerms()) {
            throw new IllegalArgumentException("You must agree to the terms and conditions (2% penalty for early withdrawal)");
        }

        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        validateOwnership(account, user);

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException(account.getAccountNumber(), request.getAmount());
        }

        // Deduct from account
        account.setBalance(account.getBalance().subtract(request.getAmount()));

        // Create transaction for the deduction
        Transaction transaction = Transaction.builder()
                .type(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .balanceAfter(account.getBalance())
                .description("Fixed Deposit - " + request.getDurationInMonths() + " months")
                .account(account)
                .timestamp(LocalDateTime.now())
                .build();

        LocalDate startDate = LocalDate.now();
        LocalDate maturityDate = startDate.plusMonths(request.getDurationInMonths());

        FixedDeposit fd = FixedDeposit.builder()
                .amount(request.getAmount())
                .durationInMonths(request.getDurationInMonths())
                .startDate(startDate)
                .maturityDate(maturityDate)
                .status(FixedDepositStatus.ACTIVE)
                .account(account)
                .build();

        accountRepository.save(account);
        transactionRepository.save(transaction);
        FixedDeposit saved = fixedDepositRepository.save(fd);

        return mapToResponse(saved);
    }

    @Transactional
    public FixedDepositResponse breakFixedDeposit(Long fdId, User user) {
        FixedDeposit fd = fixedDepositRepository.findById(fdId)
                .orElseThrow(() -> new ResourceNotFoundException("FixedDeposit", fdId));

        if (fd.getStatus() != FixedDepositStatus.ACTIVE) {
            throw new IllegalArgumentException("This fixed deposit is no longer active");
        }

        Account account = accountRepository.findByIdForUpdate(fd.getAccount().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", fd.getAccount().getId()));

        validateOwnership(account, user);

        boolean isMatured = !LocalDate.now().isBefore(fd.getMaturityDate());
        BigDecimal penaltyAmount = BigDecimal.ZERO;
        BigDecimal refundAmount;

        if (isMatured) {
            // Matured: return full amount, no penalty
            refundAmount = fd.getAmount();
            fd.setStatus(FixedDepositStatus.MATURED);
        } else {
            // Early break: 2% penalty
            penaltyAmount = fd.getAmount().multiply(new BigDecimal("0.02")).setScale(2, RoundingMode.HALF_UP);
            refundAmount = fd.getAmount().subtract(penaltyAmount);
            fd.setStatus(FixedDepositStatus.BROKEN);
        }

        fd.setPenaltyAmount(penaltyAmount);
        fd.setRefundedAmount(refundAmount);
        fd.setBrokenAt(LocalDateTime.now());

        // Credit back to account
        account.setBalance(account.getBalance().add(refundAmount));

        // Create deposit transaction
        Transaction transaction = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(refundAmount)
                .balanceAfter(account.getBalance())
                .description(isMatured ? "Fixed Deposit Matured" : "Fixed Deposit Broken (2% penalty: ₹" + penaltyAmount + ")")
                .account(account)
                .timestamp(LocalDateTime.now())
                .build();

        accountRepository.save(account);
        transactionRepository.save(transaction);
        fixedDepositRepository.save(fd);

        return mapToResponse(fd);
    }

    public List<FixedDepositResponse> getFixedDepositsByAccount(Long accountId, User user) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        validateOwnership(account, user);

        return fixedDepositRepository.findByAccountIdOrderByCreatedAtDesc(accountId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateOwnership(Account account, User user) {
        if (!account.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You do not own this account");
        }
    }

    private FixedDepositResponse mapToResponse(FixedDeposit fd) {
        return FixedDepositResponse.builder()
                .id(fd.getId())
                .amount(fd.getAmount())
                .penaltyAmount(fd.getPenaltyAmount())
                .refundedAmount(fd.getRefundedAmount())
                .durationInMonths(fd.getDurationInMonths())
                .startDate(fd.getStartDate() != null ? fd.getStartDate().toString() : null)
                .maturityDate(fd.getMaturityDate() != null ? fd.getMaturityDate().toString() : null)
                .status(fd.getStatus().name())
                .createdAt(fd.getCreatedAt() != null ? fd.getCreatedAt().toString() : null)
                .brokenAt(fd.getBrokenAt() != null ? fd.getBrokenAt().toString() : null)
                .accountId(fd.getAccount().getId())
                .accountNumber(fd.getAccount().getAccountNumber())
                .build();
    }
}
