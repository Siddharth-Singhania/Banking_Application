package com.banking.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String accountNumber, BigDecimal amount) {
        super("Insufficient funds in account " + accountNumber + " for amount " + amount);
    }
}
