package com.banking.repository;

import com.banking.model.FixedDeposit;
import com.banking.model.enums.FixedDepositStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FixedDepositRepository extends JpaRepository<FixedDeposit, Long> {
    List<FixedDeposit> findByAccountIdOrderByCreatedAtDesc(Long accountId);
    List<FixedDeposit> findByStatus(FixedDepositStatus status);
}
