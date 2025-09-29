package com.gestao.financas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gestao.financas.entity.SecurityLog;
import com.gestao.financas.repository.SecurityLogRepository;

@Service
public class SecurityLogService {

    @Autowired
    private SecurityLogRepository logRepository;

    public void logEvent(Long userId, String event, String ipAddress) {
        SecurityLog log = new SecurityLog();
        log.setUserId(userId);
        log.setEvent(event);
        log.setIpAddress(ipAddress);
        logRepository.save(log);
    }
}
