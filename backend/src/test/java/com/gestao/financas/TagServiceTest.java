package com.gestao.financas;

import com.gestao.financas.entity.Tag;
import com.gestao.financas.repository.TagRepository;
import com.gestao.financas.service.TagService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServiceTest {

    private TagRepository tagRepository;
    private TagService tagService;

    @BeforeEach
    void setUp() {
        tagRepository = mock(TagRepository.class);
        tagService = new TagService(tagRepository);
    }

    @Test
    void createTag_savesAndReturnsTag() {
        Tag tag = new Tag();
        tag.setName("Tag 1");

        when(tagRepository.save(tag)).thenReturn(tag);

        Tag result = tagService.createTag(tag);

        assertNotNull(result);
        assertEquals("Tag 1", result.getName());
        verify(tagRepository).save(tag);
    }

    @Test
    void getAllTags_returnsList() {
        Tag t1 = new Tag();
        t1.setName("Tag 1");
        Tag t2 = new Tag();
        t2.setName("Tag 2");

        when(tagRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Tag> result = tagService.getAllTags();

        assertEquals(2, result.size());
        assertEquals("Tag 1", result.get(0).getName());
        assertEquals("Tag 2", result.get(1).getName());
        verify(tagRepository).findAll();
    }

    @Test
    void getTagById_returnsTag() {
        Tag tag = new Tag();
        tag.setName("Tag 1");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        Tag result = tagService.getTagById(1L);

        assertNotNull(result);
        assertEquals("Tag 1", result.getName());
        verify(tagRepository).findById(1L);
    }

    @Test
    void getTagById_tagNotFound_throwsException() {
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tagService.getTagById(1L));
        assertEquals("Tag não encontrada", ex.getMessage());
    }

    @Test
    void updateTag_updatesAndReturnsTag() {
        Tag existing = new Tag();
        existing.setName("Old Name");

        Tag updateDetails = new Tag();
        updateDetails.setName("New Name");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(tagRepository.save(existing)).thenReturn(existing);

        Tag result = tagService.updateTag(1L, updateDetails);

        assertEquals("New Name", result.getName());
        verify(tagRepository).save(existing);
    }

    @Test
    void deleteTag_callsRepository() {
        Long id = 1L;

        doNothing().when(tagRepository).deleteById(id);

        tagService.deleteTag(id);

        verify(tagRepository).deleteById(id);
    }
}

