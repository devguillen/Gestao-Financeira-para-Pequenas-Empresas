package com.gestao.financas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestao.financas.controller.MetaController;
import com.gestao.financas.model.Meta;
import com.gestao.financas.repository.MetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MetaController.class)
class MetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MetaRepository metaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Meta meta;

    @BeforeEach
    void setUp() {
        meta = new Meta();
        meta.setId(1L);
        meta.setDescricao("Meta de Viagem");
        meta.setValorObjetivo(new BigDecimal("5000"));
        meta.setValorAtual(new BigDecimal("1000"));
    }

    @Test
    void listarMetas_returnsList() throws Exception {
        when(metaRepository.findAll()).thenReturn(List.of(meta));

        mockMvc.perform(get("/api/metas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(meta.getId()))
                .andExpect(jsonPath("$[0].descricao").value(meta.getDescricao()));
    }

    @Test
    void criarMeta_savesMeta() throws Exception {
        when(metaRepository.save(any(Meta.class))).thenReturn(meta);

        mockMvc.perform(post("/api/metas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(meta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(meta.getId()))
                .andExpect(jsonPath("$.descricao").value(meta.getDescricao()));
    }

    @Test
    void atualizarMeta_updatesMeta() throws Exception {
        Meta updatedMeta = new Meta();
        updatedMeta.setDescricao("Meta Renovada");
        updatedMeta.setValorObjetivo(new BigDecimal("6000"));
        updatedMeta.setValorAtual(new BigDecimal("1500"));

        when(metaRepository.findById(1L)).thenReturn(Optional.of(meta));
        when(metaRepository.save(any(Meta.class))).thenReturn(updatedMeta);

        mockMvc.perform(put("/api/metas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMeta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Meta Renovada"))
                .andExpect(jsonPath("$.valorObjetivo").value(6000))
                .andExpect(jsonPath("$.valorAtual").value(1500));
    }

    @Test
    void deletarMeta_deletesMeta() throws Exception {
        mockMvc.perform(delete("/api/metas/1"))
                .andExpect(status().isOk()); // ou isNoContent() se o controller retornar noContent()
    }
}