package com.banking.controller;

import com.banking.dto.AccountRequest;
import com.banking.dto.AccountResponse;
import com.banking.dto.DepositWithdrawRequest;
import com.banking.dto.LimitUpdateRequest;
import com.banking.model.Account;
import com.banking.model.User;
import com.banking.service.AccountService;
import com.banking.service.StatementExportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final StatementExportService statementExportService;

    public AccountController(AccountService accountService, StatementExportService statementExportService) {
        this.accountService = accountService;
        this.statementExportService = statementExportService;
    }



    @GetMapping
    public ResponseEntity<List<AccountResponse>> getMyAccounts() {
        User user = getCurrentUser();
        return ResponseEntity.ok(accountService.getAccountsByUser(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        User user = getCurrentUser();
        AccountResponse response = accountService.getAccountById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponse> deposit(@PathVariable Long id,
                                                    @Valid @RequestBody DepositWithdrawRequest request) {
        User user = getCurrentUser();
        return ResponseEntity.ok(accountService.deposit(id, request, user));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@PathVariable Long id,
                                                     @Valid @RequestBody DepositWithdrawRequest request) {
        User user = getCurrentUser();
        return ResponseEntity.ok(accountService.withdraw(id, request, user));
    }

    @PutMapping("/{id}/limit")
    public ResponseEntity<AccountResponse> updateDailyLimit(@PathVariable Long id,
                                                            @Valid @RequestBody LimitUpdateRequest request) {
        User user = getCurrentUser();
        return ResponseEntity.ok(accountService.updateDailyLimit(id, request, user));
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportStatement(@PathVariable Long id) throws ExecutionException, InterruptedException {
        User user = getCurrentUser();
        Account account = accountService.getAccountEntityById(id);

        if (!account.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You do not own this account");
        }

        byte[] csvBytes = statementExportService.exportStatement(id).get();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=statement_" + account.getAccountNumber() + ".csv")
                .body(csvBytes);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
