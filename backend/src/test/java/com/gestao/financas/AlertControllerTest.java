package com.gestao.financas;

import com.gestao.financas.controller.AlertController;
import com.gestao.financas.entity.Alert;
import com.gestao.financas.service.AlertService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertController.class)
class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertService alertService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Alert alert1;
    private Alert alert2;

    @BeforeEach
    void setUp() {
        alert1 = new Alert();
        alert1.setId(1L);
        alert1.setMessage("Alerta 1");
        alert1.setRead(false);

        alert2 = new Alert();
        alert2.setId(2L);
        alert2.setMessage("Alerta 2");
        alert2.setRead(false);
    }

    @Test
    void getUnreadAlerts_returnsList() throws Exception {
        List<Alert> alerts = Arrays.asList(alert1, alert2);
        Mockito.when(alertService.getUnreadAlerts(anyLong())).thenReturn(alerts);

        mockMvc.perform(get("/api/alerts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].message").value("Alerta 1"))
                .andExpect(jsonPath("$[1].message").value("Alerta 2"));
    }

    @Test
    void markAsRead_callsService() throws Exception {
        mockMvc.perform(post("/api/alerts/1/read"))
                .andExpect(status().isOk());

        Mockito.verify(alertService).markAsRead(1L);
    }
}
