package com.gestao.financas.service;

import com.gestao.financas.entity.RecurringTransaction;
import com.gestao.financas.repository.RecurringTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecurringTransactionServiceTest {

    @Mock
    private RecurringTransactionRepository repository;

    @InjectMocks
    private RecurringTransactionService service;

    private RecurringTransaction transaction1;
    private RecurringTransaction transaction2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        transaction1 = new RecurringTransaction();
        transaction1.setId(1L);
        transaction1.setAccountId(100L);
        transaction1.setAmount(BigDecimal.valueOf(200));

        transaction2 = new RecurringTransaction();
        transaction2.setId(2L);
        transaction2.setAccountId(100L);
        transaction2.setAmount(BigDecimal.valueOf(500));
    }

    @Test
    void testCreateRecurringTransaction() {
        when(repository.save(transaction1)).thenReturn(transaction1);

        RecurringTransaction created = service.createRecurringTransaction(transaction1);

        assertNotNull(created);
        assertEquals(transaction1.getId(), created.getId());
        verify(repository, times(1)).save(transaction1);
    }

    @Test
    void testGetRecurringTransactionsByAccount() {
        when(repository.findByAccountId(100L)).thenReturn(Arrays.asList(transaction1, transaction2));

        List<RecurringTransaction> transactions = service.getRecurringTransactionsByAccount(100L);

        assertNotNull(transactions);
        assertEquals(2, transactions.size());
        verify(repository, times(1)).findByAccountId(100L);
    }

    @Test
    void testDeleteRecurringTransaction() {
        doNothing().when(repository).deleteById(1L);

        service.deleteRecurringTransaction(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}
