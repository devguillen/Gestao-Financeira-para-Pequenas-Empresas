package com.gestao.financas;


import com.gestao.financas.entity.SecurityLog;
import com.gestao.financas.repository.SecurityLogRepository;
import com.gestao.financas.service.SecurityLogService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class SecurityLogServiceTest {

    private SecurityLogRepository logRepository;
    private SecurityLogService securityLogService;

    @BeforeEach
    void setUp() {
        logRepository = mock(SecurityLogRepository.class);
        securityLogService = new SecurityLogService();

        // Injetar mock via Reflection, já que o campo é private
        try {
            java.lang.reflect.Field field = SecurityLogService.class.getDeclaredField("logRepository");
            field.setAccessible(true);
            field.set(securityLogService, logRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void logEvent_savesLog() {
        Long userId = 1L;
        String event = "LOGIN_SUCCESS";
        String ipAddress = "127.0.0.1";

        securityLogService.logEvent(userId, event, ipAddress);

        // Verifica se o repositório foi chamado com o objeto correto
        verify(logRepository).save(argThat(log ->
                log.getUserId().equals(userId) &&
                log.getEvent().equals(event) &&
                log.getIpAddress().equals(ipAddress)
        ));
    }
}

