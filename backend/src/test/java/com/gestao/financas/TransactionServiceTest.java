package com.gestao.financas;

import com.gestao.financas.dto.FinancialSummaryDTO;
import com.gestao.financas.dto.TransactionChartDTO;
import com.gestao.financas.dto.TaxReportDTO;
import com.gestao.financas.entity.Transaction;
import com.gestao.financas.repository.TransactionRepository;
import com.gestao.financas.service.AlertService;
import com.gestao.financas.service.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private TransactionRepository repository;
    private AlertService alertService;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        repository = mock(TransactionRepository.class);
        alertService = mock(AlertService.class);
        transactionService = new TransactionService(repository, alertService);
    }

    @Test
    void createTransaction_savesAndTriggersAlert() {
        Transaction tx = new Transaction();
        tx.setAmount(BigDecimal.valueOf(100));
        tx.setType("expense");
        tx.setCategory("Food");

        when(repository.save(tx)).thenReturn(tx);

        Transaction saved = transactionService.createTransaction(tx);

        assertEquals(BigDecimal.valueOf(100), saved.getAmount());
        verify(repository).save(tx);
        verify(alertService).checkAndCreateAlert(tx);
    }

    @Test
    void getTransactionsByAccount_returnsList() {
        Transaction tx1 = new Transaction();
        tx1.setAmount(BigDecimal.valueOf(50));
        Transaction tx2 = new Transaction();
        tx2.setAmount(BigDecimal.valueOf(150));

        when(repository.findByAccountId(1L)).thenReturn(Arrays.asList(tx1, tx2));

        List<Transaction> result = transactionService.getTransactionsByAccount(1L);

        assertEquals(2, result.size());
        verify(repository).findByAccountId(1L);
    }

    @Test
    void getFinancialSummary_returnsTotals() {
        Transaction income = new Transaction();
        income.setType("income");
        income.setAmount(BigDecimal.valueOf(200));
        Transaction expense = new Transaction();
        expense.setType("expense");
        expense.setAmount(BigDecimal.valueOf(50));

        when(repository.findByUserAndPeriod(anyLong(), any(), any()))
                .thenReturn(Arrays.asList(income, expense));

        FinancialSummaryDTO summary = transactionService.getFinancialSummary(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertEquals(BigDecimal.valueOf(200), summary.getTotalIncome());
        assertEquals(BigDecimal.valueOf(50), summary.getTotalExpense());
    }

    @Test
    void getCategoryComparison_returnsAggregatedData() {
        Transaction t1 = new Transaction();
        t1.setCategory("Food");
        t1.setType("income");
        t1.setAmount(BigDecimal.valueOf(100));

        Transaction t2 = new Transaction();
        t2.setCategory("Food");
        t2.setType("expense");
        t2.setAmount(BigDecimal.valueOf(30));

        when(repository.findByUserAndPeriod(anyLong(), any(), any()))
                .thenReturn(Arrays.asList(t1, t2));

        List<TransactionChartDTO> chart = transactionService.getCategoryComparison(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertEquals(1, chart.size());
        TransactionChartDTO dto = chart.get(0);
        assertEquals("Food", dto.getCategory());
        assertEquals(BigDecimal.valueOf(100), dto.getIncome());
        assertEquals(BigDecimal.valueOf(30), dto.getExpense());
    }

    @Test
    void projectFutureBalance_calculatesProjection() {
        Transaction t1 = new Transaction();
        t1.setType("income");
        t1.setAmount(BigDecimal.valueOf(300));
        t1.setDate(LocalDateTime.now().minusDays(1));

        Transaction t2 = new Transaction();
        t2.setType("expense");
        t2.setAmount(BigDecimal.valueOf(100));
        t2.setDate(LocalDateTime.now().minusDays(1));

        when(repository.findByAccountUserId(1L)).thenReturn(Arrays.asList(t1, t2));

        Map<LocalDate, BigDecimal> projection = transactionService.projectFutureBalance(1L, 3);

        assertEquals(4, projection.size()); // hoje + 3 dias futuros
    }

    @Test
    void generateTaxReport_returnsAggregatedReport() {
        Transaction t1 = new Transaction();
        t1.setCategory("Food");
        t1.setType("income");
        t1.setAmount(BigDecimal.valueOf(200));

        Transaction t2 = new Transaction();
        t2.setCategory("Food");
        t2.setType("expense");
        t2.setAmount(BigDecimal.valueOf(50));

        when(repository.findByUserAndPeriod(anyLong(), any(), any()))
                .thenReturn(Arrays.asList(t1, t2));

        List<TaxReportDTO> reports = transactionService.generateTaxReport(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertEquals(1, reports.size());
        TaxReportDTO dto = reports.get(0);
        assertEquals("Food", dto.getCategory());
        assertEquals(BigDecimal.valueOf(200), dto.getTotalIncome());
        assertEquals(BigDecimal.valueOf(50), dto.getTotalExpense());
        assertEquals(BigDecimal.valueOf(22.5), dto.getTaxDue()); // (200-50)*0.15
    }
}

