package com.banking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @jakarta.validation.constraints.Pattern(regexp = "^\\d{12}$", message = "Aadhar number must be exactly 12 digits")
    private String aadharNumber;

    private java.math.BigDecimal initialBalance = java.math.BigDecimal.ZERO;

    @NotBlank
    @Size(min = 6)
    private String password;

    public RegisterRequest() {}

    public RegisterRequest(String username, String email, String password, String aadharNumber, java.math.BigDecimal initialBalance) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.aadharNumber = aadharNumber;
        this.initialBalance = initialBalance;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAadharNumber() { return aadharNumber; }
    public void setAadharNumber(String aadharNumber) { this.aadharNumber = aadharNumber; }

    public java.math.BigDecimal getInitialBalance() { return initialBalance; }
    public void setInitialBalance(java.math.BigDecimal initialBalance) { this.initialBalance = initialBalance; }
}
