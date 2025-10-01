package com.gestao.financas.dto;

import java.math.BigDecimal;

public class TransactionChartDTO {
    private String category;
    private BigDecimal income;
    private BigDecimal expense;

    public TransactionChartDTO(String category, BigDecimal income, BigDecimal expense) {
        this.category = category;
        this.income = income;
        this.expense = expense;
    }

    // Getters e Setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }

    public BigDecimal getExpense() { return expense; }
    public void setExpense(BigDecimal expense) { this.expense = expense; }
}
