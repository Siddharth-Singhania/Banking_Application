package com.banking.repository;

import com.banking.model.RecurringPayment;
import com.banking.model.enums.RecurringPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecurringPaymentRepository extends JpaRepository<RecurringPayment, Long> {
    List<RecurringPayment> findBySourceAccountIdOrderByCreatedAtDesc(Long accountId);
    List<RecurringPayment> findByStatusAndDayOfMonth(RecurringPaymentStatus status, int dayOfMonth);
}
