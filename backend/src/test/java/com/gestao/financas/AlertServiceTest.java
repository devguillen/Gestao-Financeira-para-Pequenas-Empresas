package com.gestao.financas;

import com.gestao.financas.entity.Alert;
import com.gestao.financas.entity.Transaction;
import com.gestao.financas.entity.User;
import com.gestao.financas.repository.AlertRepository;
import com.gestao.financas.repository.TransactionRepository;
import com.gestao.financas.service.AlertService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlertServiceTest {

    private AlertRepository alertRepository;
    private TransactionRepository transactionRepository;
    private AlertService alertService;

    @BeforeEach
    void setUp() {
        alertRepository = mock(AlertRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        alertService = new AlertService(alertRepository, transactionRepository);
    }

    @Test
    void checkAndCreateAlert_expenseAboveAverage_createsWarningAlert() {
        User user = new User();
        user.setId(1L);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType("expense");
        transaction.setCategory("Food");
        transaction.setAmount(BigDecimal.valueOf(200));

        Transaction previous = new Transaction();
        previous.setUser(user);
        previous.setType("expense");
        previous.setCategory("Food");
        previous.setAmount(BigDecimal.valueOf(100));

        when(transactionRepository.findByUserAndPeriod(eq(1L), any(), any()))
                .thenReturn(List.of(previous));

        alertService.checkAndCreateAlert(transaction);

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository, times(1)).save(captor.capture());

        Alert savedAlert = captor.getValue();
        assertEquals("WARNING", savedAlert.getType());
        assertTrue(savedAlert.getMessage().contains("Food"));
        assertEquals(user, savedAlert.getUser());
    }

    @Test
    void checkAndCreateAlert_incomeBelowAverage_createsInfoAlert() {
        User user = new User();
        user.setId(1L);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType("income");
        transaction.setCategory("Salary");
        transaction.setAmount(BigDecimal.valueOf(400));

        Transaction previous = new Transaction();
        previous.setUser(user);
        previous.setType("income");
        previous.setCategory("Salary");
        previous.setAmount(BigDecimal.valueOf(1000));

        when(transactionRepository.findByUserAndPeriod(eq(1L), any(), any()))
                .thenReturn(List.of(previous));

        alertService.checkAndCreateAlert(transaction);

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository, times(1)).save(captor.capture());

        Alert savedAlert = captor.getValue();
        assertEquals("INFO", savedAlert.getType());
        assertTrue(savedAlert.getMessage().contains("Salary"));
        assertEquals(user, savedAlert.getUser());
    }

    @Test
    void getUnreadAlerts_returnsAlerts() {
        Alert alert1 = new Alert();
        Alert alert2 = new Alert();
        when(alertRepository.findByUserIdAndReadFalse(1L)).thenReturn(Arrays.asList(alert1, alert2));

        List<Alert> result = alertService.getUnreadAlerts(1L);

        assertEquals(2, result.size());
        verify(alertRepository, times(1)).findByUserIdAndReadFalse(1L);
    }

    @Test
    void markAsRead_setsReadTrue() {
        Alert alert = new Alert();
        alert.setRead(false);

        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));

        alertService.markAsRead(1L);

        assertTrue(alert.isRead());
        verify(alertRepository, times(1)).save(alert);
    }

    @Test
    void markAsRead_alertNotFound_throwsException() {
        when(alertRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> alertService.markAsRead(1L));
        assertTrue(exception.getMessage().contains("Alerta não encontrado"));
    }
}



