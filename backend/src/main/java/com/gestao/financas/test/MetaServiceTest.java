package com.gestao.financas.test;

import com.gestao.financas.model.Meta;
import com.gestao.financas.repository.MetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetaServiceTest {

    @Mock
    private MetaRepository metaRepository;

    @InjectMocks
    private MetaService metaService;

    private Meta meta1;
    private Meta meta2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        meta1 = new Meta();
        meta1.setId(1L);
        meta1.setName("Meta 1");
        meta1.setValue(1000.0);

        meta2 = new Meta();
        meta2.setId(2L);
        meta2.setName("Meta 2");
        meta2.setValue(2000.0);
    }

    @Test
    void testListarMetas() {
        when(metaRepository.findAll()).thenReturn(Arrays.asList(meta1, meta2));

        List<Meta> metas = metaService.listarMetas();

        assertNotNull(metas);
        assertEquals(2, metas.size());
        verify(metaRepository, times(1)).findAll();
    }

    @Test
    void testCriarMeta() {
        when(metaRepository.save(meta1)).thenReturn(meta1);

        Meta created = metaService.criarMeta(meta1);

        assertNotNull(created);
        assertEquals("Meta 1", created.getName());
        assertEquals(1000.0, created.getValue());
        verify(metaRepository, times(1)).save(meta1);
    }
}
