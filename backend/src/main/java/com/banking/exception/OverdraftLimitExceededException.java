package com.banking.exception;

import java.math.BigDecimal;

public class OverdraftLimitExceededException extends RuntimeException {

    public OverdraftLimitExceededException(String accountNumber, BigDecimal amount, BigDecimal overdraftLimit) {
        super("Withdrawal of " + amount + " exceeds overdraft limit of " + overdraftLimit + " for account " + accountNumber);
    }
}
