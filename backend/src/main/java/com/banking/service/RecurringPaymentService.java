package com.banking.service;

import com.banking.dto.RecurringPaymentRequest;
import com.banking.dto.RecurringPaymentResponse;
import com.banking.exception.ResourceNotFoundException;
import com.banking.model.Account;
import com.banking.model.RecurringPayment;
import com.banking.model.User;
import com.banking.model.enums.RecurringPaymentStatus;
import com.banking.repository.AccountRepository;
import com.banking.repository.RecurringPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecurringPaymentService {

    private final RecurringPaymentRepository recurringPaymentRepository;
    private final AccountRepository accountRepository;

    public RecurringPaymentService(RecurringPaymentRepository recurringPaymentRepository,
                                   AccountRepository accountRepository) {
        this.recurringPaymentRepository = recurringPaymentRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public RecurringPaymentResponse createRecurringPayment(Long accountId, RecurringPaymentRequest request, User user) {
        Account sourceAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        validateOwnership(sourceAccount, user);

        RecurringPayment rp = RecurringPayment.builder()
                .sourceAccount(sourceAccount)
                .destinationAccountNumber(request.getDestinationAccountNumber())
                .amount(request.getAmount())
                .dayOfMonth(request.getDayOfMonth())
                .description(request.getDescription())
                .status(RecurringPaymentStatus.ACTIVE)
                .build();

        RecurringPayment saved = recurringPaymentRepository.save(rp);

        return mapToResponse(saved);
    }

    @Transactional
    public RecurringPaymentResponse cancelRecurringPayment(Long paymentId, User user) {
        RecurringPayment rp = recurringPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("RecurringPayment", paymentId));

        validateOwnership(rp.getSourceAccount(), user);

        if (rp.getStatus() == RecurringPaymentStatus.CANCELLED) {
            throw new IllegalArgumentException("Recurring payment is already cancelled");
        }

        rp.setStatus(RecurringPaymentStatus.CANCELLED);
        rp.setCancelledAt(LocalDateTime.now());

        RecurringPayment saved = recurringPaymentRepository.save(rp);

        return mapToResponse(saved);
    }

    public List<RecurringPaymentResponse> getRecurringPaymentsByAccount(Long accountId, User user) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        validateOwnership(account, user);

        return recurringPaymentRepository.findBySourceAccountIdOrderByCreatedAtDesc(accountId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateOwnership(Account account, User user) {
        if (!account.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You do not own this account");
        }
    }

    private RecurringPaymentResponse mapToResponse(RecurringPayment rp) {
        return RecurringPaymentResponse.builder()
                .id(rp.getId())
                .sourceAccountNumber(rp.getSourceAccount().getAccountNumber())
                .destinationAccountNumber(rp.getDestinationAccountNumber())
                .amount(rp.getAmount())
                .dayOfMonth(rp.getDayOfMonth())
                .description(rp.getDescription())
                .status(rp.getStatus().name())
                .createdAt(rp.getCreatedAt() != null ? rp.getCreatedAt().toString() : null)
                .lastExecutedAt(rp.getLastExecutedAt() != null ? rp.getLastExecutedAt().toString() : null)
                .cancelledAt(rp.getCancelledAt() != null ? rp.getCancelledAt().toString() : null)
                .build();
    }
}
