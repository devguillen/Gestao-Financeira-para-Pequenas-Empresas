package com.gestao.financas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestao.financas.model.Transacao;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
}
