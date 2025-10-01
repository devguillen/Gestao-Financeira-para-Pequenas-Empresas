package com.gestao.financas.controller;

import com.gestao.financas.dto.ConsolidatedBalanceDTO;
import com.gestao.financas.service.BalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/consolidated")
    public ResponseEntity<ConsolidatedBalanceDTO> getConsolidatedBalance(@RequestParam Long userId) {
        return ResponseEntity.ok(balanceService.getConsolidatedBalance(userId));
    }
}
