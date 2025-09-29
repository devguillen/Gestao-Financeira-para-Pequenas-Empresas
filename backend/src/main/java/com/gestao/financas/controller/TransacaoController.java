package com.gestao.financas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gestao.financas.model.Transacao;
import com.gestao.financas.repository.TransacaoRepository;

import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @GetMapping
    public List<Transacao> listarTransacoes() {
        return transacaoRepository.findAll();
    }

    @PostMapping
    public Transacao criarTransacao(@RequestBody Transacao transacao) {
        return transacaoRepository.save(transacao);
    }

    @PutMapping("/{id}")
    public Transacao atualizarTransacao(@PathVariable Long id, @RequestBody Transacao transacaoAtualizada) {
        Transacao transacao = transacaoRepository.findById(id).orElseThrow();
        transacao.setDescricao(transacaoAtualizada.getDescricao());
        transacao.setValor(transacaoAtualizada.getValor());
        transacao.setData(transacaoAtualizada.getData());
        transacao.setTipo(transacaoAtualizada.getTipo());
        return transacaoRepository.save(transacao);
    }

    @DeleteMapping("/{id}")
    public void deletarTransacao(@PathVariable Long id) {
        transacaoRepository.deleteById(id);
    }
}