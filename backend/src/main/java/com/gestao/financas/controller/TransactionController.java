package com.gestao.financas.controller;

import com.gestao.financas.dto.FinancialSummaryDTO;
import com.gestao.financas.dto.TransactionChartDTO;
import com.gestao.financas.entity.Transaction;
import com.gestao.financas.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(service.createTransaction(transaction));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(service.getTransactionsByAccount(accountId));
    }

    @GetMapping("/subtransactions/{parentId}")
    public ResponseEntity<List<Transaction>> getSubTransactions(@PathVariable Long parentId) {
        return ResponseEntity.ok(service.getSubTransactions(parentId));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> delete(@PathVariable Long transactionId) {
        service.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }

    // Resumo financeiro por período
    @GetMapping("/summary")
    public ResponseEntity<FinancialSummaryDTO> getSummary(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        FinancialSummaryDTO summary = service.getFinancialSummary(userId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    // 🔥 Gráficos comparativos: receitas vs despesas por categoria
    @GetMapping("/charts/category")
    public ResponseEntity<List<TransactionChartDTO>> getCategoryChart(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<TransactionChartDTO> chartData = service.getCategoryComparison(userId, startDate, endDate);
        return ResponseEntity.ok(chartData);
    }

    // 🔮 Projeção de saldo futuro
    @GetMapping("/balance/projection")
    public ResponseEntity<?> getFutureBalanceProjection(
            @RequestParam Long userId,
            @RequestParam int daysAhead
    ) {
        return ResponseEntity.ok(service.projectFutureBalance(userId, daysAhead));
    }
}
