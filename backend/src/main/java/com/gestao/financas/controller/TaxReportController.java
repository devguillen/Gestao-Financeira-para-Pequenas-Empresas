package com.gestao.financas.controller;

import com.gestao.financas.service.TaxReportService;
import com.gestao.financas.dto.TaxReportDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tax-report")
public class TaxReportController {

    private final TaxReportService taxReportService;

    public TaxReportController(TaxReportService taxReportService) {
        this.taxReportService = taxReportService;
    }

    // Endpoint para gerar relatório fiscal de um usuário
    @GetMapping("/{userId}")
    public TaxReportDTO generateTaxReport(@PathVariable Long userId,
                                          @RequestParam(required = false) String startDate,
                                          @RequestParam(required = false) String endDate) {
        return taxReportService.generateReport(userId, startDate, endDate);
    }
}
