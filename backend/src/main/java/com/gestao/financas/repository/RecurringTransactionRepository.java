package com.gestao.financas.repository;

import com.gestao.financas.entity.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    List<RecurringTransaction> findByAccountId(Long accountId);
}
