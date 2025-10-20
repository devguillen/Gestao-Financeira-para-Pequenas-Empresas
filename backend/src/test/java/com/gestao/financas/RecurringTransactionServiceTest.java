package com.gestao.financas;

import com.gestao.financas.entity.RecurringTransaction;
import com.gestao.financas.repository.RecurringTransactionRepository;
import com.gestao.financas.service.RecurringTransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecurringTransactionServiceTest {

    private RecurringTransactionRepository repository;
    private RecurringTransactionService service;

    @BeforeEach
    void setUp() {
        repository = mock(RecurringTransactionRepository.class);
        service = new RecurringTransactionService(repository);
    }

    @Test
    void createRecurringTransaction_savesAndReturnsTransaction() {
        RecurringTransaction transaction = new RecurringTransaction();
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setDescription("Pagamento mensal");

        when(repository.save(transaction)).thenReturn(transaction);

        RecurringTransaction result = service.createRecurringTransaction(transaction);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100), result.getAmount());
        assertEquals("Pagamento mensal", result.getDescription());
        verify(repository).save(transaction);
    }

    @Test
    void getRecurringTransactionsByAccount_returnsList() {
        Long accountId = 1L;
        RecurringTransaction t1 = new RecurringTransaction();
        t1.setDescription("Pagamento 1");
        RecurringTransaction t2 = new RecurringTransaction();
        t2.setDescription("Pagamento 2");

        when(repository.findByAccountId(accountId)).thenReturn(Arrays.asList(t1, t2));

        List<RecurringTransaction> result = service.getRecurringTransactionsByAccount(accountId);

        assertEquals(2, result.size());
        assertEquals("Pagamento 1", result.get(0).getDescription());
        assertEquals("Pagamento 2", result.get(1).getDescription());
        verify(repository).findByAccountId(accountId);
    }

    @Test
    void deleteRecurringTransaction_callsRepository() {
        Long transactionId = 1L;

        doNothing().when(repository).deleteById(transactionId);

        service.deleteRecurringTransaction(transactionId);

        verify(repository).deleteById(transactionId);
    }
}

