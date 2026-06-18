package com.banking.job;

import com.banking.model.Account;
import com.banking.model.RecurringPayment;
import com.banking.model.Transaction;
import com.banking.model.enums.RecurringPaymentStatus;
import com.banking.model.enums.TransactionType;
import com.banking.repository.AccountRepository;
import com.banking.repository.RecurringPaymentRepository;
import com.banking.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class RecurringPaymentJob {

    private static final Logger log = LoggerFactory.getLogger(RecurringPaymentJob.class);

    private final RecurringPaymentRepository recurringPaymentRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public RecurringPaymentJob(RecurringPaymentRepository recurringPaymentRepository,
                                AccountRepository accountRepository,
                                TransactionRepository transactionRepository) {
        this.recurringPaymentRepository = recurringPaymentRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void executeRecurringPayments() {
        int today = LocalDate.now().getDayOfMonth();
        List<RecurringPayment> payments = recurringPaymentRepository
                .findByStatusAndDayOfMonth(RecurringPaymentStatus.ACTIVE, today);

        log.info("Recurring payment job triggered. Found {} payments for day {}", payments.size(), today);

        for (RecurringPayment payment : payments) {
            try {
                processPayment(payment);
            } catch (Exception e) {
                log.error("Failed to process recurring payment ID {}: {}", payment.getId(), e.getMessage());
            }
        }
    }

    @Transactional
    public void processPayment(RecurringPayment payment) {
        Account source = accountRepository.findByIdForUpdate(payment.getSourceAccount().getId())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Optional<Account> destOpt = accountRepository.findByAccountNumber(payment.getDestinationAccountNumber());
        if (destOpt.isEmpty()) {
            log.warn("Destination account {} not found for recurring payment {}", 
                     payment.getDestinationAccountNumber(), payment.getId());
            return;
        }

        Account dest = accountRepository.findByIdForUpdate(destOpt.get().getId())
                .orElseThrow(() -> new RuntimeException("Destination account not found for lock"));

        BigDecimal amount = payment.getAmount();

        if (source.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient funds for recurring payment {}. Skipping.", payment.getId());
            return;
        }

        // Execute transfer
        source.setBalance(source.getBalance().subtract(amount));
        dest.setBalance(dest.getBalance().add(amount));

        LocalDateTime now = LocalDateTime.now();
        String desc = payment.getDescription() != null ? payment.getDescription() : "Recurring Payment";

        Transaction txOut = Transaction.builder()
                .type(TransactionType.TRANSFER_OUT)
                .amount(amount)
                .balanceAfter(source.getBalance())
                .description("[Recurring] " + desc + " to " + dest.getAccountNumber())
                .account(source)
                .timestamp(now)
                .build();

        Transaction txIn = Transaction.builder()
                .type(TransactionType.TRANSFER_IN)
                .amount(amount)
                .balanceAfter(dest.getBalance())
                .description("[Recurring] " + desc + " from " + source.getAccountNumber())
                .account(dest)
                .timestamp(now)
                .build();

        accountRepository.save(source);
        accountRepository.save(dest);
        transactionRepository.save(txOut);
        transactionRepository.save(txIn);

        payment.setLastExecutedAt(now);
        recurringPaymentRepository.save(payment);

        log.info("Recurring payment {} executed: ₹{} from {} to {}", 
                 payment.getId(), amount, source.getAccountNumber(), dest.getAccountNumber());
    }
}
