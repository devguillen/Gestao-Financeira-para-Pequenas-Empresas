package com.gestao.financas;

import com.gestao.financas.model.Meta;
import com.gestao.financas.repository.MetaRepository;
import com.gestao.financas.service.MetaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetaServiceTest {

    private MetaRepository metaRepository;
    private MetaService metaService;

    @BeforeEach
    void setUp() {
        metaRepository = mock(MetaRepository.class);
        metaService = new MetaService();

        // Injeção do mock usando Reflection (já que o campo é private)
        try {
            java.lang.reflect.Field field = MetaService.class.getDeclaredField("metaRepository");
            field.setAccessible(true);
            field.set(metaService, metaRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void listarMetas_returnsAllMetas() {
        Meta m1 = new Meta();
        m1.setDescricao("Meta 1");
        m1.setValorObjetivo(BigDecimal.valueOf(1000));
        m1.setValorAtual(BigDecimal.valueOf(200));

        Meta m2 = new Meta();
        m2.setDescricao("Meta 2");
        m2.setValorObjetivo(BigDecimal.valueOf(500));
        m2.setValorAtual(BigDecimal.valueOf(100));

        when(metaRepository.findAll()).thenReturn(Arrays.asList(m1, m2));

        List<Meta> result = metaService.listarMetas();

        assertEquals(2, result.size());
        assertEquals("Meta 1", result.get(0).getDescricao());
        assertEquals("Meta 2", result.get(1).getDescricao());
        verify(metaRepository).findAll();
    }

    @Test
    void criarMeta_savesAndReturnsMeta() {
        Meta meta = new Meta();
        meta.setDescricao("Nova Meta");
        meta.setValorObjetivo(BigDecimal.valueOf(2000));
        meta.setValorAtual(BigDecimal.ZERO);

        when(metaRepository.save(meta)).thenReturn(meta);

        Meta result = metaService.criarMeta(meta);

        assertNotNull(result);
        assertEquals("Nova Meta", result.getDescricao());
        assertEquals(BigDecimal.valueOf(2000), result.getValorObjetivo());
        assertEquals(BigDecimal.ZERO, result.getValorAtual());
        verify(metaRepository).save(meta);
    }
}
