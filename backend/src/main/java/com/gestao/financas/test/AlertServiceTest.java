package com.gestao.financas.test;

import com.gestao.financas.entity.Alert;
import com.gestao.financas.entity.Transaction;
import com.gestao.financas.entity.User;
import com.gestao.financas.repository.AlertRepository;
import com.gestao.financas.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AlertService alertService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
    }

    @Test
    void testExpenseAboveAverageCreatesWarningAlert() {
        Transaction past1 = new Transaction();
        past1.setUser(user);
        past1.setType("expense");
        past1.setCategory("alimentacao");
        past1.setAmount(BigDecimal.valueOf(200));
        past1.setDate(LocalDateTime.now().minusDays(10));

        Transaction past2 = new Transaction();
        past2.setUser(user);
        past2.setType("expense");
        past2.setCategory("alimentacao");
        past2.setAmount(BigDecimal.valueOf(300));
        past2.setDate(LocalDateTime.now().minusDays(5));

        when(transactionRepository.findByUserAndPeriod(eq(user.getId()), any(), any()))
                .thenReturn(Arrays.asList(past1, past2));

        Transaction newTransaction = new Transaction();
        newTransaction.setUser(user);
        newTransaction.setType("expense");
        newTransaction.setCategory("alimentacao");
        newTransaction.setAmount(BigDecimal.valueOf(600));
        newTransaction.setDate(LocalDateTime.now());

        alertService.checkAndCreateAlert(newTransaction);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository, times(1)).save(alertCaptor.capture());

        Alert alert = alertCaptor.getValue();
        assertEquals("WARNING", alert.getType());
        assertTrue(alert.getMessage().contains("alimentacao"));
    }

    @Test
    void testIncomeBelowAverageCreatesInfoAlert() {
        Transaction past1 = new Transaction();
        past1.setUser(user);
        past1.setType("income");
        past1.setCategory("salario");
        past1.setAmount(BigDecimal.valueOf(1000));
        past1.setDate(LocalDateTime.now().minusDays(15));

        Transaction past2 = new Transaction();
        past2.setUser(user);
        past2.setType("income");
        past2.setCategory("salario");
        past2.setAmount(BigDecimal.valueOf(1200));
        past2.setDate(LocalDateTime.now().minusDays(7));

        when(transactionRepository.findByUserAndPeriod(eq(user.getId()), any(), any()))
                .thenReturn(Arrays.asList(past1, past2));

        Transaction newTransaction = new Transaction();
        newTransaction.setUser(user);
        newTransaction.setType("income");
        newTransaction.setCategory("salario");
        newTransaction.setAmount(BigDecimal.valueOf(400)); // < 50% da média
        newTransaction.setDate(LocalDateTime.now());

        alertService.checkAndCreateAlert(newTransaction);

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository, times(1)).save(alertCaptor.capture());

        Alert alert = alertCaptor.getValue();
        assertEquals("INFO", alert.getType());
        assertTrue(alert.getMessage().contains("salario"));
    }

    @Test
    void testGetUnreadAlerts() {
        Alert alert1 = new Alert();
        alert1.setUser(user);
        alert1.setRead(false);

        Alert alert2 = new Alert();
        alert2.setUser(user);
        alert2.setRead(false);

        when(alertRepository.findByUserIdAndReadFalse(user.getId()))
                .thenReturn(Arrays.asList(alert1, alert2));

        List<Alert> unread = alertService.getUnreadAlerts(user.getId());
        assertEquals(2, unread.size());
    }

    @Test
    void testMarkAsRead() {
        Alert alert = new Alert();
        alert.setId(1L);
        alert.setUser(user);
        alert.setRead(false);

        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        alertService.markAsRead(1L);

        assertTrue(alert.isRead());
        verify(alertRepository, times(1)).save(alert);
    }
}
