package com.gestao.financas.repository;

import com.gestao.financas.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByParentTransactionId(Long parentTransactionId);

    // 🔥 Busca todas as transações de um usuário (dono da conta)
    List<Transaction> findByAccountUserId(Long userId);

    // 🔥 Busca transações de um usuário dentro de um período
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId AND t.date BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserAndPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
