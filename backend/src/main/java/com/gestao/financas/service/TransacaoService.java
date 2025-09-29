package com.gestao.financas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestao.financas.model.Transacao;
import com.gestao.financas.repository.TransacaoRepository;

import java.util.List;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    public List<Transacao> listarTransacoes() {
        return transacaoRepository.findAll();
    }

    public Transacao criarTransacao(Transacao transacao) {
        return transacaoRepository.save(transacao);
    }
}