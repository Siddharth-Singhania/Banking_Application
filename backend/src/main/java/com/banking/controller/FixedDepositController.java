package com.banking.controller;

import com.banking.dto.FixedDepositRequest;
import com.banking.dto.FixedDepositResponse;
import com.banking.model.User;
import com.banking.service.FixedDepositService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fixed-deposits")
public class FixedDepositController {

    private final FixedDepositService fixedDepositService;

    public FixedDepositController(FixedDepositService fixedDepositService) {
        this.fixedDepositService = fixedDepositService;
    }

    @PostMapping("/account/{accountId}")
    public ResponseEntity<FixedDepositResponse> createFixedDeposit(
            @PathVariable Long accountId,
            @Valid @RequestBody FixedDepositRequest request) {
        User user = getCurrentUser();
        return ResponseEntity.ok(fixedDepositService.createFixedDeposit(accountId, request, user));
    }

    @PostMapping("/{id}/break")
    public ResponseEntity<FixedDepositResponse> breakFixedDeposit(@PathVariable Long id) {
        User user = getCurrentUser();
        return ResponseEntity.ok(fixedDepositService.breakFixedDeposit(id, user));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<FixedDepositResponse>> getFixedDeposits(@PathVariable Long accountId) {
        User user = getCurrentUser();
        return ResponseEntity.ok(fixedDepositService.getFixedDepositsByAccount(accountId, user));
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
