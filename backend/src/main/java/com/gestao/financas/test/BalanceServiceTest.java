package com.gestao.financas.service;

import com.gestao.financas.dto.ConsolidatedBalanceDTO;
import com.gestao.financas.entity.Account;
import com.gestao.financas.entity.Transaction;
import com.gestao.financas.repository.AccountRepository;
import com.gestao.financas.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BalanceServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BalanceService balanceService;

    private Account account1;
    private Account account2;
    private Transaction t1;
    private Transaction t2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        account1 = new Account();
        account1.setBalance(BigDecimal.valueOf(1000));

        account2 = new Account();
        account2.setBalance(BigDecimal.valueOf(500));

        t1 = new Transaction();
        t1.setType("income");
        t1.setAmount(BigDecimal.valueOf(200));

        t2 = new Transaction();
        t2.setType("expense");
        t2.setAmount(BigDecimal.valueOf(100));
    }

    @Test
    void testGetConsolidatedBalance() {
        when(accountRepository.findByUserId(1L)).thenReturn(Arrays.asList(account1, account2));
        when(transactionRepository.findByAccountUserId(1L)).thenReturn(Arrays.asList(t1, t2));

        ConsolidatedBalanceDTO result = balanceService.getConsolidatedBalance(1L);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1500), result.totalBalance());
        assertEquals(BigDecimal.valueOf(200), result.totalIncome());
        assertEquals(BigDecimal.valueOf(100), result.totalExpense());

        verify(accountRepository, times(1)).findByUserId(1L);
        verify(transactionRepository, times(1)).findByAccountUserId(1L);
    }
}
