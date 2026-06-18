package com.banking.dto;

public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private String status;
    private String message;
    private String accountNumber;

    public AuthResponse() {}

    public AuthResponse(String token, String username, String role, String status, String message, String accountNumber) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.status = status;
        this.message = message;
        this.accountNumber = accountNumber;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public static class Builder {
        private String token;
        private String username;
        private String role;
        private String status;
        private String message;
        private String accountNumber;

        public Builder token(String token) { this.token = token; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder role(String role) { this.role = role; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder accountNumber(String accountNumber) { this.accountNumber = accountNumber; return this; }

        public AuthResponse build() {
            return new AuthResponse(token, username, role, status, message, accountNumber);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
