package com.gestao.financas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestao.financas.controller.TagController;
import com.gestao.financas.entity.Tag;
import com.gestao.financas.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagController.class)
class TagControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @Autowired
    private ObjectMapper objectMapper;

    private Tag tag1;
    private Tag tag2;

    @BeforeEach
    void setUp() {
        tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("Urgente");

        tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("Importante");
    }

    @Test
    void testCreateTag() throws Exception {
        when(tagService.createTag(ArgumentMatchers.any(Tag.class))).thenReturn(tag1);

        mockMvc.perform(post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tag1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Urgente"))
                .andExpect(jsonPath("$.color").value("#FF0000"));
    }

    @Test
    void testGetAllTags() throws Exception {
        List<Tag> tags = Arrays.asList(tag1, tag2);
        when(tagService.getAllTags()).thenReturn(tags);

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Urgente"))
                .andExpect(jsonPath("$[1].name").value("Importante"));
    }

    @Test
    void testGetTagById() throws Exception {
        when(tagService.getTagById(1L)).thenReturn(tag1);

        mockMvc.perform(get("/api/tags/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Urgente"));
    }

    @Test
    void testUpdateTag() throws Exception {
        Tag updatedTag = new Tag();
        updatedTag.setId(1L);
        updatedTag.setName("Atualizada");

        when(tagService.updateTag(eq(1L), any(Tag.class))).thenReturn(updatedTag);

        mockMvc.perform(put("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTag)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Atualizada"))
                .andExpect(jsonPath("$.color").value("#0000FF"));
    }

    @Test
    void testDeleteTag() throws Exception {
        doNothing().when(tagService).deleteTag(1L);

        mockMvc.perform(delete("/api/tags/1"))
                .andExpect(status().isNoContent());

        verify(tagService, times(1)).deleteTag(1L);
    }
}
