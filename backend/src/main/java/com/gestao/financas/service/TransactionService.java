package com.gestao.financas.service;

import com.gestao.financas.entity.Transaction;
import com.gestao.financas.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction createTransaction(Transaction transaction) {
        return repository.save(transaction);
    }

    public List<Transaction> getTransactionsByAccount(Long accountId) {
        return repository.findByAccountId(accountId);
    }

    public List<Transaction> getSubTransactions(Long parentTransactionId) {
        return repository.findByParentTransactionId(parentTransactionId);
    }

    public void deleteTransaction(Long transactionId) {
        repository.deleteById(transactionId);
    }
}
