package com.gestao.financas.test;

import com.gestao.financas.entity.Tag;
import com.gestao.financas.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag tag1;
    private Tag tag2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("Alimentação");

        tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("Transporte");
    }

    @Test
    void testCreateTag() {
        when(tagRepository.save(tag1)).thenReturn(tag1);

        Tag created = tagService.createTag(tag1);

        assertNotNull(created);
        assertEquals("Alimentação", created.getName());
        verify(tagRepository, times(1)).save(tag1);
    }

    @Test
    void testGetAllTags() {
        when(tagRepository.findAll()).thenReturn(Arrays.asList(tag1, tag2));

        List<Tag> tags = tagService.getAllTags();

        assertNotNull(tags);
        assertEquals(2, tags.size());
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    void testGetTagById() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag1));

        Tag tag = tagService.getTagById(1L);

        assertNotNull(tag);
        assertEquals("Alimentação", tag.getName());
        verify(tagRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateTag() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag1));
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tag updatedDetails = new Tag();
        updatedDetails.setName("Saúde");

        Tag updated = tagService.updateTag(1L, updatedDetails);

        assertNotNull(updated);
        assertEquals("Saúde", updated.getName());
        verify(tagRepository, times(1)).save(tag1);
    }

    @Test
    void testDeleteTag() {
        doNothing().when(tagRepository).deleteById(1L);

        tagService.deleteTag(1L);

        verify(tagRepository, times(1)).deleteById(1L);
    }
}
