package com.gestao.financas;

import com.gestao.financas.dto.ConsolidatedBalanceDTO;
import com.gestao.financas.entity.Account;
import com.gestao.financas.entity.Transaction;
import com.gestao.financas.repository.AccountRepository;
import com.gestao.financas.repository.TransactionRepository;
import com.gestao.financas.service.BalanceService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BalanceServiceTest {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private BalanceService balanceService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        balanceService = new BalanceService(accountRepository, transactionRepository);
    }

    @Test
    void getConsolidatedBalance_calculatesCorrectly() {
        Long userId = 1L;

        // Mockando contas
        Account acc1 = new Account();
        acc1.setBalance(BigDecimal.valueOf(1000));
        Account acc2 = new Account();
        acc2.setBalance(BigDecimal.valueOf(500));
        List<Account> accounts = Arrays.asList(acc1, acc2);

        // Mockando transações
        Transaction t1 = new Transaction();
        t1.setType("income");
        t1.setAmount(BigDecimal.valueOf(200));
        Transaction t2 = new Transaction();
        t2.setType("expense");
        t2.setAmount(BigDecimal.valueOf(150));
        Transaction t3 = new Transaction();
        t3.setType("income");
        t3.setAmount(BigDecimal.valueOf(300));
        List<Transaction> transactions = Arrays.asList(t1, t2, t3);

        when(accountRepository.findByUserId(userId)).thenReturn(accounts);
        when(transactionRepository.findByAccountUserId(userId)).thenReturn(transactions);

        ConsolidatedBalanceDTO result = balanceService.getConsolidatedBalance(userId);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1500), result.getTotalBalance()); // 1000 + 500
        assertEquals(BigDecimal.valueOf(500), result.getTotalIncome());   // 200 + 300
        assertEquals(BigDecimal.valueOf(150), result.getTotalExpense());  // 150

        verify(accountRepository).findByUserId(userId);
        verify(transactionRepository).findByAccountUserId(userId);
    }

    @Test
    void getConsolidatedBalance_noAccountsOrTransactions_returnsZero() {
        Long userId = 1L;

        when(accountRepository.findByUserId(userId)).thenReturn(List.of());
        when(transactionRepository.findByAccountUserId(userId)).thenReturn(List.of());

        ConsolidatedBalanceDTO result = balanceService.getConsolidatedBalance(userId);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalBalance());
        assertEquals(BigDecimal.ZERO, result.getTotalIncome());
        assertEquals(BigDecimal.ZERO, result.getTotalExpense());
    }
}
