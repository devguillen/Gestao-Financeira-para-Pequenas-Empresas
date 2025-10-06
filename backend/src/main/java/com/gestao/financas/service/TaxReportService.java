package com.gestao.financas.service;

import com.gestao.financas.dto.TaxReportDTO;
import com.gestao.financas.entity.Transaction;
import com.gestao.financas.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TaxReportService {

    private final TransactionRepository transactionRepository;

    public TaxReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TaxReportDTO generateReport(Long userId, String startDateStr, String endDateStr) {
        LocalDateTime startDate;
        LocalDateTime endDate;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Se datas não forem fornecidas, usar padrão último mês
        if (startDateStr != null && endDateStr != null) {
            startDate = LocalDateTime.parse(startDateStr, formatter);
            endDate = LocalDateTime.parse(endDateStr, formatter);
        } else {
            endDate = LocalDateTime.now();
            startDate = endDate.minusMonths(1);
        }

        List<Transaction> transactions = transactionRepository.findByUserAndPeriod(userId, startDate, endDate);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("income"))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("expense"))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxDue = totalIncome.subtract(totalExpense).multiply(BigDecimal.valueOf(0.15)); // Ex.: 15% imposto sobre lucro

        TaxReportDTO reportDTO = new TaxReportDTO();
        reportDTO.setTotalIncome(totalIncome);
        reportDTO.setTotalExpense(totalExpense);
        reportDTO.setTaxDue(taxDue);

        return reportDTO;
    }
}
