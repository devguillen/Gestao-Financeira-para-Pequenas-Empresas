package com.gestao.financas;

import com.gestao.financas.entity.Alert;
import com.gestao.financas.entity.Category;
import com.gestao.financas.entity.Transaction;
import com.gestao.financas.entity.User;
import com.gestao.financas.repository.AlertRepository;
import com.gestao.financas.repository.TransactionRepository;
import com.gestao.financas.service.AlertService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);

        category = new Category();
        category.setId(1L);
        category.setName("Alimentação");
    }

    @Test
    void testCheckAndCreateAlert_ExpenseAboveAverage() {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType("expense");
        transaction.setCategory(category);
        transaction.setAmount(new BigDecimal("300"));

        Transaction recent1 = new Transaction();
        recent1.setType("expense");
        recent1.setCategory(category);
        recent1.setAmount(new BigDecimal("100"));
        recent1.setUser(user);

        Transaction recent2 = new Transaction();
        recent2.setType("expense");
        recent2.setCategory(category);
        recent2.setAmount(new BigDecimal("100"));
        recent2.setUser(user);

        when(transactionRepository.findByUserAndPeriod(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(recent1, recent2));

        alertService.checkAndCreateAlert(transaction);

        verify(alertRepository, times(1)).save(any(Alert.class));
    }

    @Test
    void testCheckAndCreateAlert_IncomeBelowAverage() {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType("income");
        transaction.setCategory(category);
        transaction.setAmount(new BigDecimal("30"));

        Transaction recent1 = new Transaction();
        recent1.setType("income");
        recent1.setCategory(category);
        recent1.setAmount(new BigDecimal("100"));
        recent1.setUser(user);

        Transaction recent2 = new Transaction();
        recent2.setType("income");
        recent2.setCategory(category);
        recent2.setAmount(new BigDecimal("100"));
        recent2.setUser(user);

        when(transactionRepository.findByUserAndPeriod(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(recent1, recent2));

        alertService.checkAndCreateAlert(transaction);

        verify(alertRepository, times(1)).save(any(Alert.class));
    }

    @Test
    void testCheckAndCreateAlert_NoAlert() {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType("expense");
        transaction.setCategory(category);
        transaction.setAmount(new BigDecimal("50"));

        Transaction recent = new Transaction();
        recent.setType("expense");
        recent.setCategory(category);
        recent.setAmount(new BigDecimal("100"));
        recent.setUser(user);

        when(transactionRepository.findByUserAndPeriod(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(recent));

        alertService.checkAndCreateAlert(transaction);

        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    void testGetUnreadAlerts() {
        Alert alert1 = new Alert();
        Alert alert2 = new Alert();
        List<Alert> alerts = Arrays.asList(alert1, alert2);

        when(alertRepository.findByUserIdAndReadFalse(1L)).thenReturn(alerts);

        List<Alert> result = alertService.getUnreadAlerts(1L);

        assertEquals(2, result.size());
        verify(alertRepository, times(1)).findByUserIdAndReadFalse(1L);
    }

    @Test
    void testMarkAsRead() {
        Alert alert = new Alert();
        alert.setId(1L);
        alert.setRead(false);

        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));
        when(alertRepository.save(alert)).thenReturn(alert);

        alertService.markAsRead(1L);

        assertTrue(alert.isRead());
        verify(alertRepository, times(1)).save(alert);
    }

    @Test
    void testMarkAsRead_AlertNotFound() {
        when(alertRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                alertService.markAsRead(99L));

        assertEquals("Alerta não encontrado com ID: 99", exception.getMessage());
    }
}

