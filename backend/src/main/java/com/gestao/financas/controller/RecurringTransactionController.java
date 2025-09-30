package com.gestao.financas.controller;

import com.gestao.financas.entity.RecurringTransaction;
import com.gestao.financas.service.RecurringTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-transactions")
public class RecurringTransactionController {

    private final RecurringTransactionService service;

    public RecurringTransactionController(RecurringTransactionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RecurringTransaction> create(@RequestBody RecurringTransaction transaction) {
        return ResponseEntity.ok(service.createRecurringTransaction(transaction));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<RecurringTransaction>> getByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(service.getRecurringTransactionsByAccount(accountId));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> delete(@PathVariable Long transactionId) {
        service.deleteRecurringTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }
}
