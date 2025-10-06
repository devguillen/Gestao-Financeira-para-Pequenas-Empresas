package com.gestao.financas.service;

import com.gestao.financas.dto.FinancialSummaryDTO;
import com.gestao.financas.dto.TransactionChartDTO;
import com.gestao.financas.entity.Transaction;
import com.gestao.financas.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository repository;
    private final AlertService alertService; // ✅ Novo serviço de alertas

    public TransactionService(TransactionRepository repository, AlertService alertService) {
        this.repository = repository;
        this.alertService = alertService;
    }

    // ✅ Criação de transação com verificação de alertas inteligentes
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        Transaction saved = repository.save(transaction);
        alertService.checkAndCreateAlert(saved); // dispara alerta se necessário
        return saved;
    }

    public List<Transaction> getTransactionsByAccount(Long accountId) {
        return repository.findByAccountId(accountId);
    }

    public List<Transaction> getSubTransactions(Long parentTransactionId) {
        return repository.findByParentTransactionId(parentTransactionId);
    }

    public void deleteTransaction(Long transactionId) {
        repository.deleteById(transactionId);
    }

    // 📊 Resumo financeiro por período
    public FinancialSummaryDTO getFinancialSummary(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = repository.findByUserAndPeriod(userId, startDate, endDate);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("income"))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("expense"))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FinancialSummaryDTO(totalIncome, totalExpense);
    }

    // 📈 Gráficos comparativos: receitas vs despesas por categoria
    public List<TransactionChartDTO> getCategoryComparison(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = repository.findByUserAndPeriod(userId, startDate, endDate);

        Map<String, Map<String, BigDecimal>> grouped = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.groupingBy(
                                Transaction::getType,
                                Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                        )
                ));

        return grouped.entrySet().stream()
                .map(entry -> new TransactionChartDTO(
                        entry.getKey(),
                        entry.getValue().getOrDefault("income", BigDecimal.ZERO),
                        entry.getValue().getOrDefault("expense", BigDecimal.ZERO)
                ))
                .collect(Collectors.toList());
    }

    // 🔮 Projeção de saldo futuro baseada no histórico
    public Map<LocalDate, BigDecimal> projectFutureBalance(Long userId, int daysAhead) {
        List<Transaction> transactions = repository.findByAccountUserId(userId);

        TreeMap<LocalDate, BigDecimal> dailyBalance = new TreeMap<>();

        BigDecimal currentBalance = transactions.stream()
                .map(t -> t.getType().equalsIgnoreCase("income") ? t.getAmount() : t.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate today = LocalDate.now();
        dailyBalance.put(today, currentBalance);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("income"))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("expense"))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long daysTracked = transactions.stream()
                .map(t -> t.getDate().toLocalDate())
                .distinct()
                .count();

        BigDecimal dailyNet = daysTracked > 0
                ? totalIncome.subtract(totalExpense).divide(BigDecimal.valueOf(daysTracked), BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;

        for (int i = 1; i <= daysAhead; i++) {
            LocalDate futureDate = today.plusDays(i);
            BigDecimal previousBalance = dailyBalance.get(futureDate.minusDays(1));
            dailyBalance.put(futureDate, previousBalance.add(dailyNet));
        }

        return dailyBalance;
    }
}
