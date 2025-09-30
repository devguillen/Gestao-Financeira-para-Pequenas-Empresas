package com.gestao.financas.controller;

import com.gestao.financas.entity.Transaction;
import com.gestao.financas.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(service.createTransaction(transaction));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(service.getTransactionsByAccount(accountId));
    }

    @GetMapping("/subtransactions/{parentId}")
    public ResponseEntity<List<Transaction>> getSubTransactions(@PathVariable Long parentId) {
        return ResponseEntity.ok(service.getSubTransactions(parentId));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> delete(@PathVariable Long transactionId) {
        service.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }
}
