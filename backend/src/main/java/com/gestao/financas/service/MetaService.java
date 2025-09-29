package com.gestao.financas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestao.financas.model.Meta;
import com.gestao.financas.repository.MetaRepository;

import java.util.List;

@Service
public class MetaService {

    @Autowired
    private MetaRepository metaRepository;

    public List<Meta> listarMetas() {
        return metaRepository.findAll();
    }

    public Meta criarMeta(Meta meta) {
        return metaRepository.save(meta);
    }
}
