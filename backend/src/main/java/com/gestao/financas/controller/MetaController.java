package com.gestao.financas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gestao.financas.model.Meta;
import com.gestao.financas.repository.MetaRepository;

import java.util.List;

@RestController
@RequestMapping("/api/metas")
public class MetaController {

    @Autowired
    private MetaRepository metaRepository;

    @GetMapping
    public List<Meta> listarMetas() {
        return metaRepository.findAll();
    }

    @PostMapping
    public Meta criarMeta(@RequestBody Meta meta) {
        return metaRepository.save(meta);
    }

    @PutMapping("/{id}")
    public Meta atualizarMeta(@PathVariable Long id, @RequestBody Meta metaAtualizada) {
        Meta meta = metaRepository.findById(id).orElseThrow();
        meta.setDescricao(metaAtualizada.getDescricao());
        meta.setValorObjetivo(metaAtualizada.getValorObjetivo());
        meta.setValorAtual(metaAtualizada.getValorAtual());
        return metaRepository.save(meta);
    }

    @DeleteMapping("/{id}")
    public void deletarMeta(@PathVariable Long id) {
        metaRepository.deleteById(id);
    }
}