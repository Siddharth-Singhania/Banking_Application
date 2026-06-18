package com.banking.cache;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FrozenAccountCache {

    private final Set<Long> frozenAccounts = ConcurrentHashMap.newKeySet();

    public void freeze(Long accountId) {
        frozenAccounts.add(accountId);
    }

    public void unfreeze(Long accountId) {
        frozenAccounts.remove(accountId);
    }

    public boolean isFrozen(Long accountId) {
        return frozenAccounts.contains(accountId);
    }

    public Set<Long> getAllFrozen() {
        return Set.copyOf(frozenAccounts);
    }
}
