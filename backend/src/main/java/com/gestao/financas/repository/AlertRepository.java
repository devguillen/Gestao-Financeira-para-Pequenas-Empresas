package com.gestao.financas.repository;

import com.gestao.financas.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByUserIdAndReadFalse(Long userId);
}
