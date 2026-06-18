package com.banking.config;

import com.banking.cache.FrozenAccountCache;
import com.banking.model.Account;
import com.banking.model.User;
import com.banking.model.enums.AccountStatus;
import com.banking.model.enums.Role;
import com.banking.model.enums.UserStatus;
import com.banking.repository.AccountRepository;
import com.banking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final FrozenAccountCache frozenAccountCache;

    public DataSeeder(UserRepository userRepository, AccountRepository accountRepository,
                      PasswordEncoder passwordEncoder, FrozenAccountCache frozenAccountCache) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.frozenAccountCache = frozenAccountCache;
    }

    @Override
    public void run(String... args) {
        // Seed admin user if not present
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@bank.com")
                    .aadharNumber("000000000000")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(admin);
            logger.info("Admin user created successfully");
        }

        // Load frozen accounts into cache
        List<Account> allAccounts = accountRepository.findAll();
        for (Account account : allAccounts) {
            if (account.getStatus() == AccountStatus.FROZEN) {
                frozenAccountCache.freeze(account.getId());
            }
        }
        logger.info("Loaded {} frozen accounts into cache", frozenAccountCache.getAllFrozen().size());
    }
}
