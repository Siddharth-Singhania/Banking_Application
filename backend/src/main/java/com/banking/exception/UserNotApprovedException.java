package com.banking.exception;

public class UserNotApprovedException extends RuntimeException {

    public UserNotApprovedException(String username) {
        super("User " + username + " has not been approved by an administrator yet.");
    }
}
