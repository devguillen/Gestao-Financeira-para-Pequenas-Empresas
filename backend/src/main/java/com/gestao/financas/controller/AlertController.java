package com.gestao.financas.controller;

import com.gestao.financas.entity.Alert;
import com.gestao.financas.service.AlertService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    // Retorna alertas não lidos de um usuário
    @GetMapping("/{userId}")
    public List<Alert> getUnreadAlerts(@PathVariable Long userId) {
        return alertService.getUnreadAlerts(userId);
    }

    // Marca alerta como lido
    @PostMapping("/{alertId}/read")
    public void markAsRead(@PathVariable Long alertId) {
        alertService.markAsRead(alertId); // corrigido
    }
}
