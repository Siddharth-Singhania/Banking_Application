package com.banking.model;

import com.banking.model.enums.FixedDepositStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fixed_deposits")
public class FixedDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(precision = 19, scale = 2)
    private BigDecimal penaltyAmount;

    @Column(precision = 19, scale = 2)
    private BigDecimal refundedAmount;

    @Column(nullable = false)
    private int durationInMonths;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate maturityDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FixedDepositStatus status = FixedDepositStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime brokenAt;

    public FixedDeposit() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getPenaltyAmount() { return penaltyAmount; }
    public void setPenaltyAmount(BigDecimal penaltyAmount) { this.penaltyAmount = penaltyAmount; }

    public BigDecimal getRefundedAmount() { return refundedAmount; }
    public void setRefundedAmount(BigDecimal refundedAmount) { this.refundedAmount = refundedAmount; }

    public int getDurationInMonths() { return durationInMonths; }
    public void setDurationInMonths(int durationInMonths) { this.durationInMonths = durationInMonths; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getMaturityDate() { return maturityDate; }
    public void setMaturityDate(LocalDate maturityDate) { this.maturityDate = maturityDate; }

    public FixedDepositStatus getStatus() { return status; }
    public void setStatus(FixedDepositStatus status) { this.status = status; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getBrokenAt() { return brokenAt; }
    public void setBrokenAt(LocalDateTime brokenAt) { this.brokenAt = brokenAt; }

    public static class Builder {
        private Long id;
        private BigDecimal amount;
        private BigDecimal penaltyAmount;
        private BigDecimal refundedAmount;
        private int durationInMonths;
        private LocalDate startDate;
        private LocalDate maturityDate;
        private FixedDepositStatus status = FixedDepositStatus.ACTIVE;
        private Account account;
        private LocalDateTime createdAt;
        private LocalDateTime brokenAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder penaltyAmount(BigDecimal penaltyAmount) { this.penaltyAmount = penaltyAmount; return this; }
        public Builder refundedAmount(BigDecimal refundedAmount) { this.refundedAmount = refundedAmount; return this; }
        public Builder durationInMonths(int durationInMonths) { this.durationInMonths = durationInMonths; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder maturityDate(LocalDate maturityDate) { this.maturityDate = maturityDate; return this; }
        public Builder status(FixedDepositStatus status) { this.status = status; return this; }
        public Builder account(Account account) { this.account = account; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder brokenAt(LocalDateTime brokenAt) { this.brokenAt = brokenAt; return this; }

        public FixedDeposit build() {
            FixedDeposit fd = new FixedDeposit();
            fd.setId(id);
            fd.setAmount(amount);
            fd.setPenaltyAmount(penaltyAmount);
            fd.setRefundedAmount(refundedAmount);
            fd.setDurationInMonths(durationInMonths);
            fd.setStartDate(startDate);
            fd.setMaturityDate(maturityDate);
            fd.setStatus(status);
            fd.setAccount(account);
            fd.setCreatedAt(createdAt);
            fd.setBrokenAt(brokenAt);
            return fd;
        }
    }

    public static Builder builder() { return new Builder(); }
}
