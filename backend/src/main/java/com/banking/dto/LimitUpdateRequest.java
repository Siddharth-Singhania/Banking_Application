package com.banking.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class LimitUpdateRequest {

    @NotNull(message = "Limit cannot be null")
    @DecimalMin(value = "0.01", message = "Limit must be greater than 0")
    @DecimalMax(value = "100000.00", message = "Limit cannot exceed 1,00,000")
    private BigDecimal limit;

    public LimitUpdateRequest() {}

    public LimitUpdateRequest(BigDecimal limit) {
        this.limit = limit;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }
}
