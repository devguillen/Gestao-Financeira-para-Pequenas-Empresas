package com.gestao.financas.dto;

import java.math.BigDecimal;

public class TaxReportDTO {

    private String category;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal taxDue;

    // ✅ Construtor vazio obrigatório
    public TaxReportDTO() {
    }

    // Construtor com parâmetros (opcional)
    public TaxReportDTO(String category, BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal taxDue) {
        this.category = category;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.taxDue = taxDue;
    }

    // ===== Getters e Setters =====
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public BigDecimal getTaxDue() {
        return taxDue;
    }

    public void setTaxDue(BigDecimal taxDue) {
        this.taxDue = taxDue;
    }
}
