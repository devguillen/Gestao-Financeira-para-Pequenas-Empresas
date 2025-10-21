package com.gestao.financas;


import com.gestao.financas.controller.TransactionController;
import com.gestao.financas.dto.FinancialSummaryDTO;
import com.gestao.financas.dto.TransactionChartDTO;
import com.gestao.financas.entity.Transaction;
import com.gestao.financas.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService service;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("100.00"));
    }

    @Test
    void createTransaction_returnsTransaction() throws Exception {
        Mockito.when(service.createTransaction(any(Transaction.class))).thenReturn(transaction);

        String json = """
                {"amount":100.00}
                """;

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getTransactionsByAccount_returnsList() throws Exception {
        Mockito.when(service.getTransactionsByAccount(1L))
                .thenReturn(List.of(transaction));

        mockMvc.perform(get("/api/transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getFinancialSummary_returnsSummary() throws Exception {
        FinancialSummaryDTO summary = new FinancialSummaryDTO(new BigDecimal("500"), new BigDecimal("200"));

        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();

        Mockito.when(service.getFinancialSummary(1L, start, end)).thenReturn(summary);

        mockMvc.perform(get("/api/transactions/summary")
                        .param("userId", "1")
                        .param("startDate", start.toString())
                        .param("endDate", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(500))
                .andExpect(jsonPath("$.totalExpense").value(200))
                .andExpect(jsonPath("$.balance").value(300));
    }

    @Test
    void getCategoryChart_returnsChart() throws Exception {
        TransactionChartDTO chart = new TransactionChartDTO("Food", new BigDecimal("100"), new BigDecimal("50"));

        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();

        Mockito.when(service.getCategoryComparison(1L, start, end))
                .thenReturn(List.of(chart));

        mockMvc.perform(get("/api/transactions/charts/category")
                        .param("userId", "1")
                        .param("startDate", start.toString())
                        .param("endDate", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Food"))
                .andExpect(jsonPath("$[0].income").value(100))
                .andExpect(jsonPath("$[0].expense").value(50));
    }

    @Test
    void getFutureBalanceProjection_returnsMap() throws Exception {
        Map<LocalDate, BigDecimal> projection = new HashMap<>();
        projection.put(LocalDate.now(), new BigDecimal("1000"));

        Mockito.when(service.projectFutureBalance(1L, 5)).thenReturn(projection);

        mockMvc.perform(get("/api/transactions/balance/projection")
                        .param("userId", "1")
                        .param("daysAhead", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['" + LocalDate.now() + "']").value(1000));
    }

    @Test
    void deleteTransaction_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(service).deleteTransaction(1L);
    }
}
