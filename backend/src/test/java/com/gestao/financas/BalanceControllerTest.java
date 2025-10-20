package com.gestao.financas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestao.financas.controller.BalanceController;
import com.gestao.financas.dto.ConsolidatedBalanceDTO;
import com.gestao.financas.service.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BalanceController.class)
class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BalanceService balanceService;

    private ConsolidatedBalanceDTO dto;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        dto = new ConsolidatedBalanceDTO();
        dto.setTotalBalance(BigDecimal.valueOf(1500.00));
        dto.setTotalIncome(BigDecimal.valueOf(2500.00));
        dto.setTotalExpense(BigDecimal.valueOf(1000.00));
    }

    @Test
    void getConsolidatedBalance_returnsDto() throws Exception {
        Mockito.when(balanceService.getConsolidatedBalance(anyLong())).thenReturn(dto);

        mockMvc.perform(get("/api/balance/consolidated")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBalance").value(1500.00))
                .andExpect(jsonPath("$.totalIncome").value(2500.00))
                .andExpect(jsonPath("$.totalExpense").value(1000.00));
    }
}

