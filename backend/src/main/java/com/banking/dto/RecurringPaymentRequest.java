package com.banking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class RecurringPaymentRequest {
    @NotBlank
    private String destinationAccountNumber;

    @NotNull
    @DecimalMin("1.00")
    private BigDecimal amount;

    @Min(1)
    @Max(28)
    private int dayOfMonth;

    private String description;

    public RecurringPaymentRequest() {}

    public String getDestinationAccountNumber() { return destinationAccountNumber; }
    public void setDestinationAccountNumber(String destinationAccountNumber) { this.destinationAccountNumber = destinationAccountNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public int getDayOfMonth() { return dayOfMonth; }
    public void setDayOfMonth(int dayOfMonth) { this.dayOfMonth = dayOfMonth; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
