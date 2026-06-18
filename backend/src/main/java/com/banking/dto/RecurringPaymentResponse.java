package com.banking.dto;

import java.math.BigDecimal;

public class RecurringPaymentResponse {
    private Long id;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;
    private int dayOfMonth;
    private String description;
    private String status;
    private String createdAt;
    private String lastExecutedAt;
    private String cancelledAt;

    public RecurringPaymentResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSourceAccountNumber() { return sourceAccountNumber; }
    public void setSourceAccountNumber(String sourceAccountNumber) { this.sourceAccountNumber = sourceAccountNumber; }

    public String getDestinationAccountNumber() { return destinationAccountNumber; }
    public void setDestinationAccountNumber(String destinationAccountNumber) { this.destinationAccountNumber = destinationAccountNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public int getDayOfMonth() { return dayOfMonth; }
    public void setDayOfMonth(int dayOfMonth) { this.dayOfMonth = dayOfMonth; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getLastExecutedAt() { return lastExecutedAt; }
    public void setLastExecutedAt(String lastExecutedAt) { this.lastExecutedAt = lastExecutedAt; }

    public String getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(String cancelledAt) { this.cancelledAt = cancelledAt; }

    public static class Builder {
        private Long id;
        private String sourceAccountNumber;
        private String destinationAccountNumber;
        private BigDecimal amount;
        private int dayOfMonth;
        private String description;
        private String status;
        private String createdAt;
        private String lastExecutedAt;
        private String cancelledAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder sourceAccountNumber(String sourceAccountNumber) { this.sourceAccountNumber = sourceAccountNumber; return this; }
        public Builder destinationAccountNumber(String destinationAccountNumber) { this.destinationAccountNumber = destinationAccountNumber; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder dayOfMonth(int dayOfMonth) { this.dayOfMonth = dayOfMonth; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder createdAt(String createdAt) { this.createdAt = createdAt; return this; }
        public Builder lastExecutedAt(String lastExecutedAt) { this.lastExecutedAt = lastExecutedAt; return this; }
        public Builder cancelledAt(String cancelledAt) { this.cancelledAt = cancelledAt; return this; }

        public RecurringPaymentResponse build() {
            RecurringPaymentResponse r = new RecurringPaymentResponse();
            r.setId(id);
            r.setSourceAccountNumber(sourceAccountNumber);
            r.setDestinationAccountNumber(destinationAccountNumber);
            r.setAmount(amount);
            r.setDayOfMonth(dayOfMonth);
            r.setDescription(description);
            r.setStatus(status);
            r.setCreatedAt(createdAt);
            r.setLastExecutedAt(lastExecutedAt);
            r.setCancelledAt(cancelledAt);
            return r;
        }
    }

    public static Builder builder() { return new Builder(); }
}
