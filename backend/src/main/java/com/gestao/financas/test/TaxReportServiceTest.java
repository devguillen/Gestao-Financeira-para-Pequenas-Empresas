package com.gestao.financas.test;

import com.gestao.financas.dto.TaxReportDTO;
import com.gestao.financas.entity.Transaction;
import com.gestao.financas.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaxReportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TaxReportService taxReportService;

    private Transaction t1;
    private Transaction t2;
    private Transaction t3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Criando algumas transações fictícias
        t1 = new Transaction();
        t1.setType("income");
        t1.setAmount(BigDecimal.valueOf(500));

        t2 = new Transaction();
        t2.setType("expense");
        t2.setAmount(BigDecimal.valueOf(200));

        t3 = new Transaction();
        t3.setType("income");
        t3.setAmount(BigDecimal.valueOf(300));
    }

    @Test
    void testGenerateReportWithDates() {
        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now();

        when(transactionRepository.findByUserAndPeriod(1L, start, end))
                .thenReturn(Arrays.asList(t1, t2, t3));

        TaxReportDTO report = taxReportService.generateReport(
                1L,
                start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                end.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        assertNotNull(report);
        assertEquals(BigDecimal.valueOf(800), report.getTotalIncome()); // 500 + 300
        assertEquals(BigDecimal.valueOf(200), report.getTotalExpense());
        assertEquals(BigDecimal.valueOf(90), report.getTaxDue()); // (800 - 200) * 0.15

        verify(transactionRepository, times(1))
                .findByUserAndPeriod(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testGenerateReportWithoutDates() {
        // Simula o repositório para qualquer período
        when(transactionRepository.findByUserAndPeriod(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(t1, t2, t3));

        TaxReportDTO report = taxReportService.generateReport(1L, null, null);

        assertNotNull(report);
        assertEquals(BigDecimal.valueOf(800), report.getTotalIncome());
        assertEquals(BigDecimal.valueOf(200), report.getTotalExpense());
        assertEquals(BigDecimal.valueOf(90), report.getTaxDue());

        verify(transactionRepository, times(1))
                .findByUserAndPeriod(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
