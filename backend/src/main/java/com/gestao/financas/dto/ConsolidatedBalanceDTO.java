package com.gestao.financas.dto;

import java.math.BigDecimal;

public class ConsolidatedBalanceDTO {
    private BigDecimal totalBalance;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;

    public ConsolidatedBalanceDTO() {}

    public ConsolidatedBalanceDTO(BigDecimal totalBalance, BigDecimal totalIncome, BigDecimal totalExpense) {
        this.totalBalance = totalBalance;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }

    public BigDecimal getTotalBalance() { return totalBalance; }
    public void setTotalBalance(BigDecimal totalBalance) { this.totalBalance = totalBalance; }

    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

    public BigDecimal getTotalExpense() { return totalExpense; }
    public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }
}
