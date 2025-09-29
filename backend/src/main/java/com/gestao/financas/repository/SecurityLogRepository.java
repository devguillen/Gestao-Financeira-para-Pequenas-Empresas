package com.gestao.financas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gestao.financas.entity.SecurityLog;

public interface SecurityLogRepository extends JpaRepository<SecurityLog, Long> {
}
