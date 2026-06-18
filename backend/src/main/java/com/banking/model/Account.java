package com.banking.model;

import com.banking.model.enums.AccountStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String accountNumber;

    @Column(nullable = false)
    private String accountHolderName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal dailyTransactionLimit = new BigDecimal("50000.00");

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    @Version
    private Long version;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Account() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
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

    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class Builder {
        private Long id;
        private String accountNumber;
        private String accountHolderName;
        private BigDecimal balance = BigDecimal.ZERO;
        private BigDecimal dailyTransactionLimit = new BigDecimal("50000.00");
        private AccountStatus status = AccountStatus.ACTIVE;
        private User user;
        private Long version;
        private LocalDateTime createdAt;
        private List<Transaction> transactions = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder accountNumber(String accountNumber) { this.accountNumber = accountNumber; return this; }
        public Builder accountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; return this; }
        public Builder balance(BigDecimal balance) { this.balance = balance; return this; }
        public Builder dailyTransactionLimit(BigDecimal dailyTransactionLimit) { this.dailyTransactionLimit = dailyTransactionLimit; return this; }
        public Builder status(AccountStatus status) { this.status = status; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder version(Long version) { this.version = version; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder transactions(List<Transaction> transactions) { this.transactions = transactions; return this; }

        public Account build() {
            Account account = new Account();
            account.setId(this.id);
            account.setAccountNumber(this.accountNumber);
            account.setAccountHolderName(this.accountHolderName);
            account.setBalance(this.balance);
            account.setDailyTransactionLimit(this.dailyTransactionLimit);
            account.setStatus(this.status);
            account.setUser(this.user);
            account.setVersion(this.version);
            account.setCreatedAt(this.createdAt);
            if (this.transactions != null) account.setTransactions(this.transactions);
            return account;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
