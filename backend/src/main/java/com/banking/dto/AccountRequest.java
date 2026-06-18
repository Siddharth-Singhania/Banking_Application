package com.banking.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class AccountRequest {
    @NotBlank
    private String accountHolderName;

    private BigDecimal overdraftLimit;

    public AccountRequest() {}

    public AccountRequest(String accountHolderName, BigDecimal overdraftLimit) {
        this.accountHolderName = accountHolderName;
        this.overdraftLimit = overdraftLimit;
    }

    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }

    public BigDecimal getOverdraftLimit() { return overdraftLimit; }
    public void setOverdraftLimit(BigDecimal overdraftLimit) { this.overdraftLimit = overdraftLimit; }
}
