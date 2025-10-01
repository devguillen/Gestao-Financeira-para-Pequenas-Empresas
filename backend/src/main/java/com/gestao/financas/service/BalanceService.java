package com.gestao.financas.service;

import com.gestao.financas.dto.ConsolidatedBalanceDTO;
import com.gestao.financas.entity.Account;
import com.gestao.financas.entity.Transaction;
import com.gestao.financas.repository.AccountRepository;
import com.gestao.financas.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BalanceService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public BalanceService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public ConsolidatedBalanceDTO getConsolidatedBalance(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        List<Transaction> transactions = transactionRepository.findByAccountUserId(userId);

        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("income"))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("expense"))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ConsolidatedBalanceDTO(totalBalance, totalIncome, totalExpense);
    }
}
