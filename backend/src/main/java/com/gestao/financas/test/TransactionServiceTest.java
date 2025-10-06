package com.gestao.financas.service;

import com.gestao.financas.dto.FinancialSummaryDTO;
import com.gestao.financas.dto.TransactionChartDTO;
import com.gestao.financas.dto.TaxReportDTO;
import com.gestao.financas.entity.Alert;
import com.gestao.financas.entity.Transaction;
import com.gestao.financas.entity.User;
import com.gestao.financas.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction t1;
    private Transaction t2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        t1 = new Transaction();
        t1.setId(1L);
        t1.setType("income");
        t1.setCategory("Salary");
        t1.setAmount(BigDecimal.valueOf(1000));
        t1.setUser(new User());

        t2 = new Transaction();
        t2.setId(2L);
        t2.setType("expense");
        t2.setCategory("Food");
        t2.setAmount(BigDecimal.valueOf(200));
        t2.setUser(new User());
    }

    @Test
    void testCreateTransaction() {
        when(repository.save(t1)).thenReturn(t1);

        Transaction saved = transactionService.createTransaction(t1);

        assertNotNull(saved);
        assertEquals(BigDecimal.valueOf(1000), saved.getAmount());
        verify(alertService, times(1)).checkAndCreateAlert(t1);
        verify(repository, times(1)).save(t1);
    }

    @Test
    void testGetTransactionsByAccount() {
        when(repository.findByAccountId(1L)).thenReturn(Arrays.asList(t1, t2));

        List<Transaction> transactions = transactionService.getTransactionsByAccount(1L);

        assertEquals(2, transactions.size());
        verify(repository, times(1)).findByAccountId(1L);
    }

    @Test
    void testFinancialSummary() {
        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now();

        when(repository.findByUserAndPeriod(1L, start, end)).thenReturn(Arrays.asList(t1, t2));

        FinancialSummaryDTO summary = transactionService.getFinancialSummary(1L, start, end);

        assertEquals(BigDecimal.valueOf(1000), summary.getTotalIncome());
        assertEquals(BigDecimal.valueOf(200), summary.getTotalExpense());
    }

    @Test
    void testCategoryComparison() {
        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now();

        when(repository.findByUserAndPeriod(1L, start, end)).thenReturn(Arrays.asList(t1, t2));

        List<TransactionChartDTO> chart = transactionService.getCategoryComparison(1L, start, end);

        assertEquals(2, chart.size());
        chart.forEach(dto -> assertNotNull(dto.getCategory()));
    }

    @Test
    void testGenerateTaxReport() {
        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now();

        when(repository.findByUserAndPeriod(1L, start, end)).thenReturn(Arrays.asList(t1, t2));

        List<TaxReportDTO> report = transactionService.generateTaxReport(1L, start, end);

        assertEquals(2, report.size());
        for (TaxReportDTO dto : report) {
            assertNotNull(dto.getCategory());
            assertNotNull(dto.getTaxDue());
        }
    }
}
