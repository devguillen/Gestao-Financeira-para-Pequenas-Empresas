package com.gestao.financas.service;

import com.gestao.financas.entity.Alert;
import com.gestao.financas.entity.Transaction;
import com.gestao.financas.entity.User;
import com.gestao.financas.repository.AlertRepository;
import com.gestao.financas.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final TransactionRepository transactionRepository;

    public AlertService(AlertRepository alertRepository, TransactionRepository transactionRepository) {
        this.alertRepository = alertRepository;
        this.transactionRepository = transactionRepository;
    }

    public void checkAndCreateAlert(Transaction transaction) {
        User user = transaction.getUser();
        if (user == null) return; // evita NullPointerException
        Long userId = user.getId();

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Transaction> recentTransactions = transactionRepository.findByUserAndPeriod(
                userId, thirtyDaysAgo, LocalDateTime.now()
        );

        BigDecimal totalAmount = recentTransactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase(transaction.getType()) &&
                             t.getCategory().equals(transaction.getCategory()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long count = recentTransactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase(transaction.getType()) &&
                             t.getCategory().equals(transaction.getCategory()))
                .count();

        BigDecimal averageAmount = count > 0
                ? totalAmount.divide(BigDecimal.valueOf(count), BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;

        if (averageAmount.compareTo(BigDecimal.ZERO) == 0) return;

        // 🚨 Gasto > 150% da média
        if (transaction.getType().equalsIgnoreCase("expense") &&
                transaction.getAmount().compareTo(averageAmount.multiply(BigDecimal.valueOf(1.5))) > 0) {

            Alert alert = new Alert();
            alert.setUser(user);
            alert.setType("WARNING");
            alert.setMessage("Gasto em " + transaction.getCategory() + " acima da média!");
            alertRepository.save(alert);
        }

        // 📉 Receita < 50% da média
        if (transaction.getType().equalsIgnoreCase("income") &&
                transaction.getAmount().compareTo(averageAmount.multiply(BigDecimal.valueOf(0.5))) < 0) {

            Alert alert = new Alert();
            alert.setUser(user);
            alert.setType("INFO");
            alert.setMessage("Receita em " + transaction.getCategory() + " abaixo da média.");
            alertRepository.save(alert);
        }
    }

    public List<Alert> getUnreadAlerts(Long userId) {
        return alertRepository.findByUserIdAndReadFalse(userId);
    }

    public void markAsRead(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado com ID: " + alertId));
        alert.setRead(true);
        alertRepository.save(alert);
    }
}
