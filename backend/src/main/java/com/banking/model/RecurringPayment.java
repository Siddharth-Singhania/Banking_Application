package com.banking.model;

import com.banking.model.enums.RecurringPaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recurring_payments")
public class RecurringPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;

    @Column(nullable = false)
    private String destinationAccountNumber;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private int dayOfMonth;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecurringPaymentStatus status = RecurringPaymentStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastExecutedAt;

    private LocalDateTime cancelledAt;

    public RecurringPayment() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Account getSourceAccount() { return sourceAccount; }
    public void setSourceAccount(Account sourceAccount) { this.sourceAccount = sourceAccount; }

    public String getDestinationAccountNumber() { return destinationAccountNumber; }
    public void setDestinationAccountNumber(String destinationAccountNumber) { this.destinationAccountNumber = destinationAccountNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public int getDayOfMonth() { return dayOfMonth; }
    public void setDayOfMonth(int dayOfMonth) { this.dayOfMonth = dayOfMonth; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public RecurringPaymentStatus getStatus() { return status; }
    public void setStatus(RecurringPaymentStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastExecutedAt() { return lastExecutedAt; }
    public void setLastExecutedAt(LocalDateTime lastExecutedAt) { this.lastExecutedAt = lastExecutedAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public static class Builder {
        private Long id;
        private Account sourceAccount;
        private String destinationAccountNumber;
        private BigDecimal amount;
        private int dayOfMonth;
        private String description;
        private RecurringPaymentStatus status = RecurringPaymentStatus.ACTIVE;
        private LocalDateTime createdAt;
        private LocalDateTime lastExecutedAt;
        private LocalDateTime cancelledAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder sourceAccount(Account sourceAccount) { this.sourceAccount = sourceAccount; return this; }
        public Builder destinationAccountNumber(String destinationAccountNumber) { this.destinationAccountNumber = destinationAccountNumber; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder dayOfMonth(int dayOfMonth) { this.dayOfMonth = dayOfMonth; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder status(RecurringPaymentStatus status) { this.status = status; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder lastExecutedAt(LocalDateTime lastExecutedAt) { this.lastExecutedAt = lastExecutedAt; return this; }
        public Builder cancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; return this; }

        public RecurringPayment build() {
            RecurringPayment rp = new RecurringPayment();
            rp.setId(id);
            rp.setSourceAccount(sourceAccount);
            rp.setDestinationAccountNumber(destinationAccountNumber);
            rp.setAmount(amount);
            rp.setDayOfMonth(dayOfMonth);
            rp.setDescription(description);
            rp.setStatus(status);
            rp.setCreatedAt(createdAt);
            rp.setLastExecutedAt(lastExecutedAt);
            rp.setCancelledAt(cancelledAt);
            return rp;
        }
    }

    public static Builder builder() { return new Builder(); }
}
