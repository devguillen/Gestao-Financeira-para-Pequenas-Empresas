package com.gestao.financas;

import com.gestao.financas.dto.TaxReportDTO;
import com.gestao.financas.entity.Transaction;
import com.gestao.financas.repository.TransactionRepository;
import com.gestao.financas.service.TaxReportService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaxReportServiceTest {

    private TransactionRepository transactionRepository;
    private TaxReportService taxReportService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        taxReportService = new TaxReportService(transactionRepository);
    }

    @Test
    void generateReport_calculatesCorrectTotals() {
        Long userId = 1L;

        Transaction t1 = new Transaction();
        t1.setType("income");
        t1.setAmount(BigDecimal.valueOf(2000));

        Transaction t2 = new Transaction();
        t2.setType("expense");
        t2.setAmount(BigDecimal.valueOf(500));

        List<Transaction> transactions = Arrays.asList(t1, t2);

        LocalDateTime start = LocalDateTime.now().minusMonths(1);
        LocalDateTime end = LocalDateTime.now();

        when(transactionRepository.findByUserAndPeriod(eq(userId), any(), any()))
                .thenReturn(transactions);

        TaxReportDTO report = taxReportService.generateReport(userId, null, null);

        assertEquals(BigDecimal.valueOf(2000), report.getTotalIncome());
        assertEquals(BigDecimal.valueOf(500), report.getTotalExpense());
        assertEquals(BigDecimal.valueOf(225), report.getTaxDue()); // (2000 - 500) * 0.15

        verify(transactionRepository).findByUserAndPeriod(eq(userId), any(), any());
    }

    @Test
    void generateReport_withProvidedDates() {
        Long userId = 1L;
        String start = "2025-09-01 00:00:00";
        String end = "2025-09-30 23:59:59";

        Transaction t = new Transaction();
        t.setType("income");
        t.setAmount(BigDecimal.valueOf(1000));

        when(transactionRepository.findByUserAndPeriod(eq(userId), any(), any()))
                .thenReturn(Arrays.asList(t));

        TaxReportDTO report = taxReportService.generateReport(userId, start, end);

        assertEquals(BigDecimal.valueOf(1000), report.getTotalIncome());
        assertEquals(BigDecimal.ZERO, report.getTotalExpense());
        assertEquals(BigDecimal.valueOf(150), report.getTaxDue()); // 15% de 1000

        verify(transactionRepository).findByUserAndPeriod(eq(userId), any(), any());
    }

    @Test
    void generateReport_noTransactions_returnsZero() {
        Long userId = 1L;

        when(transactionRepository.findByUserAndPeriod(eq(userId), any(), any()))
                .thenReturn(Arrays.asList());

        TaxReportDTO report = taxReportService.generateReport(userId, null, null);

        assertEquals(BigDecimal.ZERO, report.getTotalIncome());
        assertEquals(BigDecimal.ZERO, report.getTotalExpense());
        assertEquals(BigDecimal.ZERO, report.getTaxDue());
    }
}

