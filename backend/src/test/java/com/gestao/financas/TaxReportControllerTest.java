package com.gestao.financas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestao.financas.controller.TaxReportController;
import com.gestao.financas.dto.TaxReportDTO;
import com.gestao.financas.service.TaxReportService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaxReportController.class)
class TaxReportControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaxReportService taxReportService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnTaxReportWithDates() throws Exception {
        // Arrange
        TaxReportDTO mockReport = new TaxReportDTO(
                "Serviços",
                new BigDecimal("10000.00"),
                new BigDecimal("4000.00"),
                new BigDecimal("600.00")
        );

        Mockito.when(taxReportService.generateReport(1L, "2025-01-01", "2025-12-31"))
                .thenReturn(mockReport);

        // Act & Assert
        mockMvc.perform(get("/api/tax-report/1")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Serviços"))
                .andExpect(jsonPath("$.totalIncome").value(10000.00))
                .andExpect(jsonPath("$.totalExpense").value(4000.00))
                .andExpect(jsonPath("$.taxDue").value(600.00));
    }

    @Test
    void shouldReturnTaxReportWithoutDates() throws Exception {
        // Arrange
        TaxReportDTO mockReport = new TaxReportDTO(
                "Investimentos",
                new BigDecimal("15000.00"),
                new BigDecimal("5000.00"),
                new BigDecimal("1200.00")
        );

        Mockito.when(taxReportService.generateReport(2L, null, null))
                .thenReturn(mockReport);

        // Act & Assert
        mockMvc.perform(get("/api/tax-report/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Investimentos"))
                .andExpect(jsonPath("$.totalIncome").value(15000.00))
                .andExpect(jsonPath("$.totalExpense").value(5000.00))
                .andExpect(jsonPath("$.taxDue").value(1200.00));
    }
}
