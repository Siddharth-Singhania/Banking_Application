package com.banking.dto;

import java.math.BigDecimal;

public class FixedDepositResponse {
    private Long id;
    private BigDecimal amount;
    private BigDecimal penaltyAmount;
    private BigDecimal refundedAmount;
    private int durationInMonths;
    private String startDate;
    private String maturityDate;
    private String status;
    private String createdAt;
    private String brokenAt;
    private Long accountId;
    private String accountNumber;

    public FixedDepositResponse() {}

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

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getMaturityDate() { return maturityDate; }
    public void setMaturityDate(String maturityDate) { this.maturityDate = maturityDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getBrokenAt() { return brokenAt; }
    public void setBrokenAt(String brokenAt) { this.brokenAt = brokenAt; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public static class Builder {
        private Long id;
        private BigDecimal amount;
        private BigDecimal penaltyAmount;
        private BigDecimal refundedAmount;
        private int durationInMonths;
        private String startDate;
        private String maturityDate;
        private String status;
        private String createdAt;
        private String brokenAt;
        private Long accountId;
        private String accountNumber;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder penaltyAmount(BigDecimal penaltyAmount) { this.penaltyAmount = penaltyAmount; return this; }
        public Builder refundedAmount(BigDecimal refundedAmount) { this.refundedAmount = refundedAmount; return this; }
        public Builder durationInMonths(int durationInMonths) { this.durationInMonths = durationInMonths; return this; }
        public Builder startDate(String startDate) { this.startDate = startDate; return this; }
        public Builder maturityDate(String maturityDate) { this.maturityDate = maturityDate; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder createdAt(String createdAt) { this.createdAt = createdAt; return this; }
        public Builder brokenAt(String brokenAt) { this.brokenAt = brokenAt; return this; }
        public Builder accountId(Long accountId) { this.accountId = accountId; return this; }
        public Builder accountNumber(String accountNumber) { this.accountNumber = accountNumber; return this; }

        public FixedDepositResponse build() {
            FixedDepositResponse r = new FixedDepositResponse();
            r.setId(id);
            r.setAmount(amount);
            r.setPenaltyAmount(penaltyAmount);
            r.setRefundedAmount(refundedAmount);
            r.setDurationInMonths(durationInMonths);
            r.setStartDate(startDate);
            r.setMaturityDate(maturityDate);
            r.setStatus(status);
            r.setCreatedAt(createdAt);
            r.setBrokenAt(brokenAt);
            r.setAccountId(accountId);
            r.setAccountNumber(accountNumber);
            return r;
        }
    }

    public static Builder builder() { return new Builder(); }
}
