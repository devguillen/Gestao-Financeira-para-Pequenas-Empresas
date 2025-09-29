package com.gestao.financas.dto;

import jakarta.validation.constraints.NotNull;

public class EnableMfaRequest {
    
    @NotNull
    private Long userId;

    // Getter e Setter
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}