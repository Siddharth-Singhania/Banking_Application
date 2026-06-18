package com.banking.controller;

import com.banking.dto.RecurringPaymentRequest;
import com.banking.dto.RecurringPaymentResponse;
import com.banking.model.User;
import com.banking.service.RecurringPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-payments")
public class RecurringPaymentController {

    private final RecurringPaymentService recurringPaymentService;

    public RecurringPaymentController(RecurringPaymentService recurringPaymentService) {
        this.recurringPaymentService = recurringPaymentService;
    }

    @PostMapping("/account/{accountId}")
    public ResponseEntity<RecurringPaymentResponse> createRecurringPayment(
            @PathVariable Long accountId,
            @Valid @RequestBody RecurringPaymentRequest request) {
        User user = getCurrentUser();
        return ResponseEntity.ok(recurringPaymentService.createRecurringPayment(accountId, request, user));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<RecurringPaymentResponse> cancelRecurringPayment(@PathVariable Long id) {
        User user = getCurrentUser();
        return ResponseEntity.ok(recurringPaymentService.cancelRecurringPayment(id, user));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<RecurringPaymentResponse>> getRecurringPayments(@PathVariable Long accountId) {
        User user = getCurrentUser();
        return ResponseEntity.ok(recurringPaymentService.getRecurringPaymentsByAccount(accountId, user));
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
