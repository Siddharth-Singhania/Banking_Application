package com.banking.service;

import com.banking.model.Transaction;
import com.banking.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class StatementExportService {

    private final TransactionRepository transactionRepository;

    public StatementExportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Async("taskExecutor")
    public CompletableFuture<byte[]> exportStatement(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByTimestampDesc(accountId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        writer.println("ID,Type,Amount,Balance After,Description,Timestamp");
        for (Transaction tx : transactions) {
            writer.printf("%d,%s,%s,%s,%s,%s%n",
                    tx.getId(),
                    tx.getType(),
                    tx.getAmount(),
                    tx.getBalanceAfter(),
                    tx.getDescription() != null ? tx.getDescription().replace(",", ";") : "",
                    tx.getTimestamp());
        }
        writer.flush();

        return CompletableFuture.completedFuture(out.toByteArray());
    }
}
