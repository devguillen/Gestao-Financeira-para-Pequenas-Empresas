package com.gestao.financas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestao.financas.entity.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
}
