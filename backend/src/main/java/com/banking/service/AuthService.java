package com.banking.service;

import com.banking.dto.AuthResponse;
import com.banking.dto.LoginRequest;
import com.banking.dto.RegisterRequest;
import com.banking.exception.UserNotApprovedException;
import com.banking.model.User;
import com.banking.model.enums.Role;
import com.banking.model.enums.UserStatus;
import com.banking.repository.UserRepository;
import com.banking.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.banking.model.Account;
import com.banking.repository.AccountRepository;
import java.util.UUID;
import java.math.BigDecimal;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager,
                       AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.accountRepository = accountRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByAadharNumber(request.getAadharNumber())) {
            throw new IllegalArgumentException("Aadhar number is already registered to another account");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .aadharNumber(request.getAadharNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .status(UserStatus.PENDING)
                .build();

        userRepository.save(user);

        String generatedAccountNumber = String.format("%010d", Math.abs(UUID.randomUUID().getMostSignificantBits() % 10000000000L));

        Account account = Account.builder()
                .accountNumber(generatedAccountNumber)
                .accountHolderName(user.getUsername())
                .balance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO)
                .user(user)
                .build();
        accountRepository.save(account);

        return AuthResponse.builder()
                .username(user.getUsername())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .accountNumber(generatedAccountNumber)
                .message("Registration successful. Please wait for admin approval.")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword()));

            User user = (User) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(user);
            
            String accNum = null;
            if (user.getRole() == Role.CUSTOMER) {
                accNum = accountRepository.findByUserId(user.getId()).stream().findFirst().map(Account::getAccountNumber).orElse(null);
            }

            return AuthResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .status(user.getStatus().name())
                    .accountNumber(accNum)
                    .message("Login successful")
                    .build();
        } catch (DisabledException e) {
            throw new UserNotApprovedException(request.getIdentifier());
        }
    }
}
