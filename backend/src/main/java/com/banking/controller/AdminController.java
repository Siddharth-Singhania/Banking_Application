package com.banking.controller;

import com.banking.cache.FrozenAccountCache;
import com.banking.dto.AccountResponse;
import com.banking.dto.UserResponse;
import com.banking.exception.ResourceNotFoundException;
import com.banking.model.Account;
import com.banking.model.User;
import com.banking.model.enums.AccountStatus;
import com.banking.model.enums.UserStatus;
import com.banking.repository.AccountRepository;
import com.banking.repository.UserRepository;
import com.banking.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final FrozenAccountCache frozenAccountCache;
    private final AccountService accountService;

    public AdminController(AccountRepository accountRepository, UserRepository userRepository,
                           FrozenAccountCache frozenAccountCache, AccountService accountService) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.frozenAccountCache = frozenAccountCache;
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountRepository.findAll().stream()
                .map(accountService::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/accounts/{id}/freeze")
    public ResponseEntity<AccountResponse> freezeAccount(@PathVariable Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", id));

        account.setStatus(AccountStatus.FROZEN);
        accountRepository.save(account);
        frozenAccountCache.freeze(id);

        return ResponseEntity.ok(accountService.mapToResponse(account));
    }

    @PostMapping("/accounts/{id}/unfreeze")
    public ResponseEntity<AccountResponse> unfreezeAccount(@PathVariable Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", id));

        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
        frozenAccountCache.unfreeze(id);

        return ResponseEntity.ok(accountService.mapToResponse(account));
    }

    @GetMapping("/users/pending")
    public ResponseEntity<List<UserResponse>> getPendingUsers() {
        List<UserResponse> users = userRepository.findByStatus(UserStatus.PENDING).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users/{id}/approve")
    public ResponseEntity<UserResponse> approveUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        return ResponseEntity.ok(mapToUserResponse(user));
    }

    @PostMapping("/users/{id}/reject")
    public ResponseEntity<UserResponse> rejectUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        user.setStatus(UserStatus.REJECTED);
        userRepository.save(user);

        return ResponseEntity.ok(mapToUserResponse(user));
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
