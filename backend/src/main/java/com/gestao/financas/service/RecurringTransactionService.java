package com.gestao.financas.service;

import com.gestao.financas.entity.RecurringTransaction;
import com.gestao.financas.repository.RecurringTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecurringTransactionService {

    private final RecurringTransactionRepository repository;

    public RecurringTransactionService(RecurringTransactionRepository repository) {
        this.repository = repository;
    }

    public RecurringTransaction createRecurringTransaction(RecurringTransaction transaction) {
        return repository.save(transaction);
    }

    public List<RecurringTransaction> getRecurringTransactionsByAccount(Long accountId) {
        return repository.findByAccountId(accountId);
    }

    public void deleteRecurringTransaction(Long transactionId) {
        repository.deleteById(transactionId);
    }
}
