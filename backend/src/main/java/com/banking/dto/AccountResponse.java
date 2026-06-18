package com.banking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountResponse {
    private Long id;
    private String accountNumber;
    private String accountHolderName;
    private BigDecimal balance;
    private BigDecimal dailyTransactionLimit;
    private String status;
    private LocalDateTime createdAt;

    public AccountResponse() {}

    public AccountResponse(Long id, String accountNumber, String accountHolderName, BigDecimal balance, BigDecimal dailyTransactionLimit, String status, LocalDateTime createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.dailyTransactionLimit = dailyTransactionLimit;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public BigDecimal getDailyTransactionLimit() { return dailyTransactionLimit; }
    public void setDailyTransactionLimit(BigDecimal dailyTransactionLimit) { this.dailyTransactionLimit = dailyTransactionLimit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class Builder {
        private Long id;
        private String accountNumber;
        private String accountHolderName;
        private BigDecimal balance;
        private BigDecimal dailyTransactionLimit;
        private String status;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder accountNumber(String accountNumber) { this.accountNumber = accountNumber; return this; }
        public Builder accountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; return this; }
        public Builder balance(BigDecimal balance) { this.balance = balance; return this; }
        public Builder dailyTransactionLimit(BigDecimal dailyTransactionLimit) { this.dailyTransactionLimit = dailyTransactionLimit; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public AccountResponse build() {
            return new AccountResponse(id, accountNumber, accountHolderName, balance, dailyTransactionLimit, status, createdAt);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
