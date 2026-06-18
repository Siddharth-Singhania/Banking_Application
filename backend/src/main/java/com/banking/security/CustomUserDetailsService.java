package com.banking.security;

import com.banking.repository.UserRepository;
import com.banking.repository.AccountRepository;
import com.banking.model.Account;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public CustomUserDetailsService(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // First try finding by username (for admin)
        return userRepository.findByUsername(identifier)
                .orElseGet(() -> {
                    // If not found, try finding by account number (for customers)
                    Account account = accountRepository.findByAccountNumber(identifier)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + identifier));
                    return account.getUser();
                });
    }
}
